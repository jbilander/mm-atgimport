package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
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

    public List<Leg> fetch(int raceCardId) {

        List<Leg> legs = new ArrayList<>();

        String sql = "select l.Id, l.RaceId, l.RaceCardId from leg l inner join race r on l.RaceId = r.Id where r.HasParticipants = 1 and l.RaceCardId = ? order by l.LegNumber";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, raceCardId);

                ResultSet rs = ps.executeQuery();

                while (rs != null && rs.next()) {

                    Leg leg = new Leg();
                    leg.setId(rs.getInt(1));
                    leg.setRaceId(rs.getInt(2));
                    leg.setRaceCardId(rs.getInt(3));
                    legs.add(leg);
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return legs;
    }
}
