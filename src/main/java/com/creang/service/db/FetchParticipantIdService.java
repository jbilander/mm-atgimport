package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchParticipantIdService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public List<Integer> fetch(int raceId) {

        List<Integer> integers = new ArrayList<>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement prepStmnt = null;

        try {

            conn = poolManager.getValidConnection(3);

            prepStmnt = conn.prepareStatement("select Id from participant where RaceId = ?");
            prepStmnt.setInt(1, raceId);

            rs = prepStmnt.executeQuery();

            while (rs != null && rs.next()) {
                integers.add(rs.getInt(1));
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(prepStmnt);
            poolManager.release(conn);
        }

        return integers;
    }
}
