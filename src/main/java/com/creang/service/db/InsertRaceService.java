package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.MiniConnectionPoolManager;
import com.creang.model.Race;

import java.sql.*;
import java.util.Set;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class InsertRaceService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public void insert(Set<Race> races) {

        String prepStmnt1 = "insert ignore into race (RaceDayDate, PostTime, RaceNumber, AtgTrackId, AtgTrackCode, TrackName) values (?, ?, ?, ?, ?, ?)";
        Connection conn = null;

        try {

            conn = poolManager.getValidConnection(3);
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(prepStmnt1)) {

                for (Race race : races) {
                    stmt1.setDate(1, Date.valueOf(race.getRaceDayDate()));
                    stmt1.setTime(2, Time.valueOf(race.getPostTime()));
                    stmt1.setInt(3, race.getRaceNumber());
                    stmt1.setInt(4, race.getAtgTrackId());
                    stmt1.setString(5, race.getAtgTrackCode());
                    stmt1.setString(6, race.getTrackName());
                    stmt1.addBatch();
                }

                stmt1.executeBatch();
                conn.commit();

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
