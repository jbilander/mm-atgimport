package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchRaceIdService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public Integer fetch(LocalDate raceDayDate, int atgTrackId, String atgTrackCode, int raceNumber) {

        String sql = "select Id from race where RaceDayDate = ? and AtgTrackId = ? and AtgTrackCode = ? and RaceNumber = ? limit 1";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(raceDayDate));
                ps.setInt(2, atgTrackId);
                ps.setString(3, atgTrackCode);
                ps.setInt(4, raceNumber);

                ResultSet rs = ps.executeQuery();

                if (rs != null && rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return null;
    }
}
