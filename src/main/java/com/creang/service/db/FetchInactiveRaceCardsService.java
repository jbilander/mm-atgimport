package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.RaceCard;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchInactiveRaceCardsService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public List<RaceCard> fetch(LocalDate raceDayDate) {

        List<RaceCard> raceCards = new ArrayList<>();

        String sql = "select Id, BetType, AtgTrackId, AtgTrackCode, RaceDayDate from racecard where Activated = 0 and RaceDayDate >= ? order by RaceDayDate, AtgTrackId, AtgTrackCode";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(raceDayDate));

                ResultSet rs = ps.executeQuery();

                while (rs != null && rs.next()) {

                    RaceCard raceCard = new RaceCard();
                    raceCard.setId(rs.getInt(1));
                    raceCard.setBetType(rs.getString(2));
                    raceCard.setAtgTrackId(rs.getInt(3));
                    raceCard.setAtgTrackCode(rs.getString(4));
                    raceCard.setRaceDayDate(rs.getDate(5).toLocalDate());
                    raceCards.add(raceCard);
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return raceCards;
    }
}
