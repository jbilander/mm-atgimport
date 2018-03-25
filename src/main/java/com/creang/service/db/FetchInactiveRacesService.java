package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Race;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchInactiveRacesService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public List<Race> fetch(LocalDate raceDayDate) {

        List<Race> races = new ArrayList<>();

        String sql = "select Id, RaceDayDate, RaceNumber, AtgTrackId, AtgTrackCode from race where HasParticipants = 0 and RaceDayDate >= ? order by RaceDayDate, AtgTrackId, RaceNumber";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(raceDayDate));

                ResultSet rs = ps.executeQuery();

                while (rs != null && rs.next()) {

                    Race race = new Race();
                    race.setId(rs.getInt(1));
                    race.setRaceDayDate(rs.getDate(2).toLocalDate());
                    race.setRaceNumber(rs.getInt(3));
                    race.setAtgTrackId(rs.getInt(4));
                    race.setAtgTrackCode(rs.getString(5));
                    races.add(race);
                }
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return races;
    }
}
