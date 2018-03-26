package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Leg;
import com.creang.model.RaceCard;

import java.sql.*;
import java.util.Set;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class InsertRaceCardService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public void insert(Set<RaceCard> raceCards) {

        String sql1 = "insert ignore into racecard (BetType, AtgTrackId, AtgTrackCode, RaceDayDate, HasBoost, TrackName) values (?, ?, ?, ?, ?, ?)";
        String sql2 = "insert ignore into leg (RaceId, RaceCardId, LegNumber, LegName) values (?, ?, ?, ?)";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {

                    for (RaceCard raceCard : raceCards) {

                        ps1.setString(1, raceCard.getBetType());
                        ps1.setInt(2, raceCard.getAtgTrackId());
                        ps1.setString(3, raceCard.getAtgTrackCode());
                        ps1.setDate(4, Date.valueOf(raceCard.getRaceDayDate()));
                        ps1.setBoolean(5, raceCard.isHasBoost());
                        ps1.setString(6, raceCard.getTrackName());
                        ps1.executeUpdate();

                        //add legs
                        try (ResultSet rs = ps1.getGeneratedKeys()) {

                            if (rs != null && rs.next()) {

                                int id = rs.getInt(1);

                                for (Leg leg : raceCard.getLegs()) {

                                    leg.setRaceCardId(id);
                                    ps2.setInt(1, leg.getRaceId());
                                    ps2.setInt(2, leg.getRaceCardId());
                                    ps2.setInt(3, leg.getLegNumber());
                                    ps2.setString(4, leg.getLegName());
                                    ps2.addBatch();
                                }
                                ps2.executeBatch();
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.severe(e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }
}
