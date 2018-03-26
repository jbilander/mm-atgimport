package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
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

    public void update(List<RaceCard> raceCards) {

        String sql1 = "insert into legparticipant (LegId, ParticipantId) values (?,?)";
        String sql2 = "update racecard set Activated = 1, Updated = ? where id = ?";

        Connection conn = null;

        try {

            conn = connectionPoolHelper.getDataSource().getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {

                    for (RaceCard raceCard : raceCards) {

                        if (raceCard.isActivated()) {

                            for (Leg leg : raceCard.getLegs()) {
                                for (LegParticipant lp : leg.getLegParticipants()) {
                                    ps1.setInt(1, lp.getLegId());
                                    ps1.setInt(2, lp.getParticipantId());
                                    ps1.addBatch();
                                }
                            }
                            ps1.executeBatch();

                            ps2.setString(1, Util.getCurrentLocalDateTimeString());
                            ps2.setInt(2, raceCard.getId());
                            ps2.executeUpdate();
                        }
                    }
                    conn.commit();
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
