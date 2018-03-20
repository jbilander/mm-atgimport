package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;
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
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public List<RaceCard> fetch(LocalDate raceDayDate) {

        List<RaceCard> raceCards = new ArrayList<>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement prepStmnt = null;

        try {

            conn = poolManager.getValidConnection(3);
            prepStmnt = conn.prepareStatement("select Id, BetType, AtgTrackId, AtgTrackCode, RaceDayDate from racecard where Activated = 0 and RaceDayDate >= ? order by RaceDayDate, AtgTrackId, AtgTrackCode");
            prepStmnt.setDate(1, Date.valueOf(raceDayDate));

            rs = prepStmnt.executeQuery();

            while (rs != null && rs.next()) {

                RaceCard raceCard = new RaceCard();
                raceCard.setId(rs.getInt(1));
                raceCard.setBetType(rs.getString(2));
                raceCard.setAtgTrackId(rs.getInt(3));
                raceCard.setAtgTrackCode(rs.getString(4));
                raceCard.setRaceDayDate(rs.getDate(5).toLocalDate());
                raceCards.add(raceCard);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeResultSet(rs);
            DbUtil.closeStatement(prepStmnt);
            poolManager.release(conn);
        }

        return raceCards;
    }
}
