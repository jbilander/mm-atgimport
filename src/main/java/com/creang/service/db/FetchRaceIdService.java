package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchRaceIdService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public Integer fetch(LocalDate raceDayDate, int atgTrackId, String atgTrackCode, int raceNumber) {

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement prepStmnt = null;

        try {

            conn = poolManager.getValidConnection(3);

            prepStmnt = conn.prepareStatement("select Id from race where RaceDayDate = ? and AtgTrackId = ? and AtgTrackCode = ? and RaceNumber = ? limit 1");

            prepStmnt.setDate(1, Date.valueOf(raceDayDate));
            prepStmnt.setInt(2, atgTrackId);
            prepStmnt.setString(3, atgTrackCode);
            prepStmnt.setInt(4, raceNumber);

            rs = prepStmnt.executeQuery();

            if (rs != null && rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(prepStmnt);
            poolManager.release(conn);
        }

        return null;
    }
}
