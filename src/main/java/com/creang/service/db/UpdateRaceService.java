package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Participant;
import com.creang.model.Race;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class UpdateRaceService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public void update(List<Race> races) {

        String sql1 = "update race set Updated = ?, HasResult = ?, TrackState = ?, TrackSurface=?, WinTurnOver = ? where id = ?";
        String sql2 = "update participant set Scratched = ?, DriverChanged = ?, WinnerOdds = ?, InvestmentWinner = ?, CardWeight = ?, CardWeightChanged = ? where id = ?";
        String sql3 = "update horse set ForeShoes = ?, HindShoes = ? where id = ?";
        String sql4 = "update driver set KeyId = ?, FirstName = ?, LastName = ?, ShortName = ?, Amateur = ?, ApprenticeAmateur = ?, ApprenticePro = ? where id = ?";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    try (PreparedStatement ps3 = conn.prepareStatement(sql3)) {
                        try (PreparedStatement ps4 = conn.prepareStatement(sql4)) {

                            for (Race race : races) {

                                ps1.setString(1, Util.getLocalDateTimeString(race.getUpdated()));
                                ps1.setBoolean(2, race.isHasResult());
                                ps1.setString(3, race.getTrackState());
                                ps1.setString(4, race.getTrackSurface());
                                ps1.setBigDecimal(5, race.getWinTurnOver());
                                ps1.setInt(6, race.getId());
                                ps1.addBatch();

                                for (Participant participant : race.getParticipants()) {

                                    ps2.setBoolean(1, participant.isScratched());
                                    ps2.setBoolean(2, participant.isDriverChanged());
                                    ps2.setBigDecimal(3, participant.getWinnerOdds());
                                    ps2.setBigDecimal(4, participant.getInvestmentWinner());
                                    ps2.setBigDecimal(5, participant.getCardWeight());
                                    ps2.setBoolean(6, participant.isCardWeightChanged());
                                    ps2.setInt(7, participant.getId());
                                    ps2.addBatch();

                                    ps3.setInt(1, participant.getHorse().getForeShoes());
                                    ps3.setInt(2, participant.getHorse().getHindShoes());
                                    ps3.setInt(3, participant.getHorse().getId());
                                    ps3.addBatch();

                                    if (participant.isDriverChanged()) {

                                        ps4.setInt(1, participant.getDriver().getKeyId());
                                        ps4.setString(2, participant.getDriver().getFirstName());
                                        ps4.setString(3, participant.getDriver().getLastName());
                                        ps4.setString(4, participant.getDriver().getShortName());
                                        ps4.setBoolean(5, participant.getDriver().isAmateur());
                                        ps4.setBoolean(6, participant.getDriver().isApprenticeAmateur());
                                        ps4.setBoolean(7, participant.getDriver().isApprenticePro());
                                        ps4.setInt(8, participant.getDriver().getId());
                                        ps4.addBatch();
                                    }
                                }
                            }
                            ps4.executeBatch();
                            ps3.executeBatch();
                            ps2.executeBatch();
                            ps1.executeBatch();
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.severe(e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }
}
