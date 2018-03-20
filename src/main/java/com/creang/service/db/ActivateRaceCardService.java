package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.db.MiniConnectionPoolManager;
import com.creang.model.Leg;
import com.creang.model.LegParticipant;
import com.creang.model.RaceCard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class ActivateRaceCardService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public void update(List<RaceCard> raceCards) {

        String prepStmnt1 = "insert into legparticipant (LegId, ParticipantId) values (?,?)";
        String prepStmnt2 = "update racecard set Activated = 1, Updated = ? where id = ?";

        Connection conn = null;

        try {

            conn = poolManager.getValidConnection(3);
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(prepStmnt1)) {
                try (PreparedStatement stmt2 = conn.prepareStatement(prepStmnt2)) {

                    for (RaceCard raceCard : raceCards) {

                        if (raceCard.isActivated()) {

                            for (Leg leg : raceCard.getLegs()) {
                                for (LegParticipant lp : leg.getLegParticipants()) {
                                    stmt1.setInt(1, lp.getLegId());
                                    stmt1.setInt(2, lp.getParticipantId());
                                    stmt1.addBatch();
                                }
                            }
                            stmt1.executeBatch();

                            stmt2.setString(1, Util.getCurrentLocalDateTimeString());
                            stmt2.setInt(2, raceCard.getId());
                            stmt2.executeUpdate();
                        }
                    }
                    conn.commit();
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
