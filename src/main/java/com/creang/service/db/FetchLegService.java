package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;
import com.creang.model.Leg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchLegService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public List<Leg> fetch(int raceCardId) {

        List<Leg> legs = new ArrayList<>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement prepStmnt = null;

        try {

            conn = poolManager.getValidConnection(3);

            prepStmnt = conn.prepareStatement("select l.Id, l.RaceId, l.RaceCardId from leg l inner join race r on l.RaceId = r.Id where r.HasParticipants = 1 and l.RaceCardId = ? order by l.LegNumber");
            prepStmnt.setInt(1, raceCardId);

            rs = prepStmnt.executeQuery();

            while (rs != null && rs.next()) {

                Leg leg = new Leg();
                leg.setId(rs.getInt(1));
                leg.setRaceId(rs.getInt(2));
                leg.setRaceCardId(rs.getInt(3));
                legs.add(leg);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(prepStmnt);
            poolManager.release(conn);
        }
        return legs;
    }
}
