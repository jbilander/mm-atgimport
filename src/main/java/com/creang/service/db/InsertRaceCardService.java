package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;
import com.creang.model.Leg;
import com.creang.model.RaceCard;

import java.sql.*;
import java.util.Set;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class InsertRaceCardService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public void insert(Set<RaceCard> raceCards) {

        String prepStmnt1 = "insert ignore into racecard (BetType, AtgTrackId, AtgTrackCode, RaceDayDate, HasBoost, TrackName) values (?, ?, ?, ?, ?, ?)";
        String prepStmnt2 = "insert ignore into leg (RaceId, RaceCardId, LegNumber, LegName) values (?, ?, ?, ?)";

        Connection conn = null;
        ResultSet rs = null;

        try {

            conn = poolManager.getValidConnection(3);
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(prepStmnt1, Statement.RETURN_GENERATED_KEYS)) {
                try (PreparedStatement stmt2 = conn.prepareStatement(prepStmnt2)) {

                    for (RaceCard raceCard : raceCards) {

                        stmt1.setString(1, raceCard.getBetType());
                        stmt1.setInt(2, raceCard.getAtgTrackId());
                        stmt1.setString(3, raceCard.getAtgTrackCode());
                        stmt1.setDate(4, Date.valueOf(raceCard.getRaceDayDate()));
                        stmt1.setBoolean(5, raceCard.isHasBoost());
                        stmt1.setString(6, raceCard.getTrackName());
                        stmt1.executeUpdate();

                        //add legs
                        rs = stmt1.getGeneratedKeys();

                        if (rs != null && rs.next()) {

                            int id = rs.getInt(1);

                            for (Leg leg : raceCard.getLegs()) {
                                leg.setRaceCardId(id);
                                stmt2.setInt(1, leg.getRaceId());
                                stmt2.setInt(2, leg.getRaceCardId());
                                stmt2.setInt(3, leg.getLegNumber());
                                stmt2.setString(4, leg.getLegName());
                                stmt2.addBatch();
                            }
                            stmt2.executeBatch();
                        }
                    }
                    conn.commit();
                }
            } catch (SQLException ex) {
                conn.rollback();
                logger.severe(ex.getMessage());
            }

            conn.setAutoCommit(true);

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeResultSet(rs);
            poolManager.release(conn);
        }
    }
}
