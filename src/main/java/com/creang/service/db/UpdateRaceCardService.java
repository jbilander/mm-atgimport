package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.db.MiniConnectionPoolManager;
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
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public void update(List<RaceCard> raceCards) {

        String prepStmnt1 = "update racecard set JackpotSum = ?, TurnOver = ?, Updated = ?, MadeBetsQuantity = ?, BoostNumber = ?, HasResult = ?, HasCompleteResult = ? where Id = ?";
        String prepStmnt2 = "update leg set HasResult = ?, LuckyHorse = ?, ReserveOrder = ?, MarksQuantity = ?, SystemsLeft = ?, Value = ? where Id = ?";
        String prepStmnt3 = "update legparticipant set Percentage = ?, Quantity = ?, LegWinner = ? where LegId = ? and ParticipantId = ?";
        String prepStmnt4 = "insert into payout (RaceCardId, NumberOfCorrects, NumberOfSystems, PayOutAmount, TotalAmount) values (?, ?, ?, ?, ?) on duplicate key update NumberOfSystems = ?, PayOutAmount = ?, TotalAmount = ?";
        Connection conn = null;

        try {

            conn = poolManager.getValidConnection(3);
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(prepStmnt1)) {
                try (PreparedStatement stmt2 = conn.prepareStatement(prepStmnt2)) {
                    try (PreparedStatement stmt3 = conn.prepareStatement(prepStmnt3)) {
                        try (PreparedStatement stmt4 = conn.prepareStatement(prepStmnt4)) {

                            for (RaceCard raceCard : raceCards) {

                                stmt1.setBigDecimal(1, raceCard.getJackpotSum() != null ? raceCard.getJackpotSum() : BigDecimal.ZERO);
                                stmt1.setBigDecimal(2, raceCard.getTurnOver() != null ? raceCard.getTurnOver() : BigDecimal.ZERO);
                                stmt1.setString(3, Util.getLocalDateTimeString(raceCard.getUpdated()));
                                stmt1.setInt(4, raceCard.getMadeBetsQuantity());
                                stmt1.setObject(5, raceCard.getBoostNumber(), Types.INTEGER);
                                stmt1.setBoolean(6, raceCard.isHasResult());
                                stmt1.setBoolean(7, raceCard.isHasCompleteResult());
                                stmt1.setInt(8, raceCard.getId());
                                stmt1.addBatch();

                                for (Leg leg : raceCard.getLegs()) {

                                    if (leg.getMarksQuantity() > 0) {
                                        stmt2.setBoolean(1, leg.isHasResult());
                                        stmt2.setObject(2, leg.getLuckyHorse(), Types.INTEGER);
                                        stmt2.setString(3, leg.getReserveOrder());
                                        stmt2.setInt(4, leg.getMarksQuantity());
                                        stmt2.setBigDecimal(5, leg.getSystemsLeft());
                                        stmt2.setBigDecimal(6, leg.getValue());
                                        stmt2.setInt(7, leg.getId());
                                        stmt2.addBatch();

                                        for (LegParticipant lp : leg.getLegParticipants()) {
                                            stmt3.setBigDecimal(1, lp.getPercentage() != null ? lp.getPercentage() : BigDecimal.ZERO);
                                            stmt3.setInt(2, lp.getQuantity());
                                            stmt3.setBoolean(3, lp.isLegWinner());
                                            stmt3.setInt(4, lp.getLegId());
                                            stmt3.setInt(5, lp.getParticipantId());
                                            stmt3.addBatch();
                                        }
                                    }
                                }

                                for (PayOut payOut : raceCard.getPayOuts()) {
                                    stmt4.setInt(1, payOut.getRaceCardId());
                                    stmt4.setInt(2, payOut.getNumberOfCorrects());
                                    stmt4.setBigDecimal(3, payOut.getNumberOfSystems());
                                    stmt4.setBigDecimal(4, payOut.getPayOutAmount());
                                    stmt4.setBigDecimal(5, payOut.getTotalAmount());
                                    stmt4.setBigDecimal(6, payOut.getNumberOfSystems());
                                    stmt4.setBigDecimal(7, payOut.getPayOutAmount());
                                    stmt4.setBigDecimal(8, payOut.getTotalAmount());
                                    stmt4.addBatch();
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
            } catch (SQLException ex) {
                conn.rollback();
                logger.severe(ex.getMessage());
            }

            conn.setAutoCommit(true);

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            poolManager.release(conn);
        }
    }
}
