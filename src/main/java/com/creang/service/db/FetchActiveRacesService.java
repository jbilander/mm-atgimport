package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Race;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchActiveRacesService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public Map<Integer, List<Race>> fetch(LocalDate fromDate, LocalDate toDate) {

        Map<Integer, List<Race>> raceMap = new LinkedHashMap<>();

        String sql = "select Id, RaceDayDate, RaceNumber, AtgTrackId, AtgTrackCode from race where RaceDayDate between ? and ? and HasParticipants = 1 and HasResult = 0 order by RaceDayDate, AtgTrackId, AtgTrackCode, RaceNumber";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(fromDate));
                ps.setDate(2, Date.valueOf(toDate));

                ResultSet rs = ps.executeQuery();

                while (rs != null && rs.next()) {

                    Race race = new Race();
                    race.setId(rs.getInt(1));
                    race.setRaceDayDate(rs.getDate(2).toLocalDate());
                    race.setRaceNumber(rs.getInt(3));
                    race.setAtgTrackId(rs.getInt(4));
                    race.setAtgTrackCode(rs.getString(5));

                    int key = Objects.hash(race.getRaceDayDate(), race.getAtgTrackId(), race.getAtgTrackCode());

                    if (!raceMap.containsKey(key)) {
                        raceMap.put(key, new ArrayList<>());
                    }

                    raceMap.get(key).add(race);
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return raceMap;
    }
}
