package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;

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

    public List<Integer> fetch(int raceId) {

        List<Integer> integers = new ArrayList<>();

        String sql = "select Id from participant where RaceId = ?";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, raceId);

                ResultSet rs = ps.executeQuery();

                while (rs != null && rs.next()) {
                    integers.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return integers;
    }
}
