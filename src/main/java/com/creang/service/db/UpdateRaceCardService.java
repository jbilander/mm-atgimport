package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Leg;
import com.creang.model.LegParticipant;
import com.creang.model.PayOut;
import com.creang.model.RaceCard;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class UpdateRaceCardService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public void update(List<RaceCard> raceCards) {

        String sql1 = "update racecard set JackpotSum = ?, TurnOver = ?, Updated = ?, MadeBetsQuantity = ?, BoostNumber = ?, HasResult = ?, HasCompleteResult = ? where Id = ?";
        String sql2 = "update leg set HasResult = ?, LuckyHorse = ?, ReserveOrder = ?, MarksQuantity = ?, SystemsLeft = ?, Value = ? where Id = ?";
        String sql3 = "update legparticipant set Percentage = ?, Quantity = ?, LegWinner = ? where LegId = ? and ParticipantId = ?";
        String sql4 = "insert into payout (RaceCardId, NumberOfCorrects, NumberOfSystems, PayOutAmount, TotalAmount) values (?, ?, ?, ?, ?) on duplicate key update NumberOfSystems = ?, PayOutAmount = ?, TotalAmount = ?";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    try (PreparedStatement ps3 = conn.prepareStatement(sql3)) {
                        try (PreparedStatement ps4 = conn.prepareStatement(sql4)) {

                            for (RaceCard raceCard : raceCards) {

                                ps1.setBigDecimal(1, raceCard.getJackpotSum() != null ? raceCard.getJackpotSum() : BigDecimal.ZERO);
                                ps1.setBigDecimal(2, raceCard.getTurnOver() != null ? raceCard.getTurnOver() : BigDecimal.ZERO);
                                ps1.setString(3, Util.getLocalDateTimeString(raceCard.getUpdated()));
                                ps1.setInt(4, raceCard.getMadeBetsQuantity());
                                ps1.setObject(5, raceCard.getBoostNumber(), Types.INTEGER);
                                ps1.setBoolean(6, raceCard.isHasResult());
                                ps1.setBoolean(7, raceCard.isHasCompleteResult());
                                ps1.setInt(8, raceCard.getId());
                                ps1.addBatch();

                                for (Leg leg : raceCard.getLegs()) {

                                    if (leg.getMarksQuantity() > 0) {

                                        ps2.setBoolean(1, leg.isHasResult());
                                        ps2.setObject(2, leg.getLuckyHorse(), Types.INTEGER);
                                        ps2.setString(3, leg.getReserveOrder());
                                        ps2.setInt(4, leg.getMarksQuantity());
                                        ps2.setBigDecimal(5, leg.getSystemsLeft());
                                        ps2.setBigDecimal(6, leg.getValue());
                                        ps2.setInt(7, leg.getId());
                                        ps2.addBatch();

                                        for (LegParticipant lp : leg.getLegParticipants()) {

                                            ps3.setBigDecimal(1, lp.getPercentage() != null ? lp.getPercentage() : BigDecimal.ZERO);
                                            ps3.setInt(2, lp.getQuantity());
                                            ps3.setBoolean(3, lp.isLegWinner());
                                            ps3.setInt(4, lp.getLegId());
                                            ps3.setInt(5, lp.getParticipantId());
                                            ps3.addBatch();
                                        }
                                    }
                                }

                                for (PayOut payOut : raceCard.getPayOuts()) {

                                    ps4.setInt(1, payOut.getRaceCardId());
                                    ps4.setInt(2, payOut.getNumberOfCorrects());
                                    ps4.setBigDecimal(3, payOut.getNumberOfSystems());
                                    ps4.setBigDecimal(4, payOut.getPayOutAmount());
                                    ps4.setBigDecimal(5, payOut.getTotalAmount());
                                    ps4.setBigDecimal(6, payOut.getNumberOfSystems());
                                    ps4.setBigDecimal(7, payOut.getPayOutAmount());
                                    ps4.setBigDecimal(8, payOut.getTotalAmount());
                                    ps4.addBatch();
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
