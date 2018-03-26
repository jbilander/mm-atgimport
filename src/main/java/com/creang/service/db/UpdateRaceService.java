package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
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

        Connection conn = null;

        try {

            conn = connectionPoolHelper.getDataSource().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(sql1)) {
                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    try (PreparedStatement stmt3 = conn.prepareStatement(sql3)) {
                        try (PreparedStatement stmt4 = conn.prepareStatement(sql4)) {

                            for (Race race : races) {

                                stmt1.setString(1, Util.getLocalDateTimeString(race.getUpdated()));
                                stmt1.setBoolean(2, race.isHasResult());
                                stmt1.setString(3, race.getTrackState());
                                stmt1.setString(4, race.getTrackSurface());
                                stmt1.setBigDecimal(5, race.getWinTurnOver());
                                stmt1.setInt(6, race.getId());
                                stmt1.addBatch();

                                for (Participant participant : race.getParticipants()) {

                                    stmt2.setBoolean(1, participant.isScratched());
                                    stmt2.setBoolean(2, participant.isDriverChanged());
                                    stmt2.setBigDecimal(3, participant.getWinnerOdds());
                                    stmt2.setBigDecimal(4, participant.getInvestmentWinner());
                                    stmt2.setBigDecimal(5, participant.getCardWeight());
                                    stmt2.setBoolean(6, participant.isCardWeightChanged());
                                    stmt2.setInt(7, participant.getId());
                                    stmt2.addBatch();

                                    stmt3.setInt(1, participant.getHorse().getForeShoes());
                                    stmt3.setInt(2, participant.getHorse().getHindShoes());
                                    stmt3.setInt(3, participant.getHorse().getId());
                                    stmt3.addBatch();

                                    if (participant.isDriverChanged()) {

                                        stmt4.setInt(1, participant.getDriver().getKeyId());
                                        stmt4.setString(2, participant.getDriver().getFirstName());
                                        stmt4.setString(3, participant.getDriver().getLastName());
                                        stmt4.setString(4, participant.getDriver().getShortName());
                                        stmt4.setBoolean(5, participant.getDriver().isAmateur());
                                        stmt4.setBoolean(6, participant.getDriver().isApprenticeAmateur());
                                        stmt4.setBoolean(7, participant.getDriver().isApprenticePro());
                                        stmt4.setInt(8, participant.getDriver().getId());
                                        stmt4.addBatch();
                                    }
                                }
                            }
                            stmt4.executeBatch();
                            stmt3.executeBatch();
                            stmt2.executeBatch();
                            stmt1.executeBatch();
                            conn.commit();
                        }
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                logger.severe(e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeConnection(conn);
        }
    }
}
