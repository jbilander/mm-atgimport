package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Driver;
import com.creang.model.Horse;
import com.creang.model.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchParticipantService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public List<Participant> fetch(int raceId) {

        List<Participant> participants = new ArrayList<>();

        String sql = "select Id, RaceId, StartNumber, Scratched from participant where RaceId = ? order by StartNumber";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, raceId);

                ResultSet rs = ps.executeQuery();

                while (rs != null && rs.next()) {

                    Participant participant = new Participant();
                    participant.setId(rs.getInt(1));
                    participant.setRaceId(rs.getInt(2));
                    participant.setStartNumber(rs.getInt(3));
                    participant.setScratched(rs.getBoolean(4));

                    Horse horse = new Horse();
                    horse.setId(participant.getId());
                    participant.setHorse(horse);

                    Driver driver = new Driver();
                    driver.setId(participant.getId());
                    participant.setDriver(driver);

                    participants.add(participant);
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return participants;
    }
}
