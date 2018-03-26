package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Race;

import java.sql.*;
import java.util.Set;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class InsertRaceService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public void insert(Set<Race> races) {

        String sql = "insert ignore into race (RaceDayDate, PostTime, RaceNumber, AtgTrackId, AtgTrackCode, TrackName) values (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (Race race : races) {

                    ps.setDate(1, Date.valueOf(race.getRaceDayDate()));
                    ps.setTime(2, Time.valueOf(race.getPostTime()));
                    ps.setInt(3, race.getRaceNumber());
                    ps.setInt(4, race.getAtgTrackId());
                    ps.setString(5, race.getAtgTrackCode());
                    ps.setString(6, race.getTrackName());
                    ps.addBatch();
                }

                ps.executeBatch();
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
