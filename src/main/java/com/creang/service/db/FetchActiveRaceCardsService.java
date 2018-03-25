package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.model.Leg;
import com.creang.model.LegParticipant;
import com.creang.model.RaceCard;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class FetchActiveRaceCardsService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public List<RaceCard> fetch(LocalDate fromDate, LocalDate toDate) {

        List<RaceCard> raceCards = new ArrayList<>();

        String sql1 = "select Id, BetType, AtgTrackId, AtgTrackCode, RaceDayDate from racecard where RaceDayDate between ? and ? and Activated = 1 and HasCompleteResult = 0 order by RaceDayDate, AtgTrackId, AtgTrackCode";
        String sql2 = "select Id, RaceId, RaceCardId, LegNumber from leg where RaceCardId = ? order by LegNumber";
        String sql3 = "select lp.ParticipantId, p.StartNumber from legparticipant lp inner join participant p on lp.ParticipantId = p.Id where lp.LegId = ? order by StartNumber";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    try (PreparedStatement ps3 = conn.prepareStatement(sql3)) {

                        ps1.setDate(1, Date.valueOf(fromDate));
                        ps1.setDate(2, Date.valueOf(toDate));
                        ResultSet rs1 = ps1.executeQuery();

                        while (rs1 != null && rs1.next()) {

                            RaceCard raceCard = new RaceCard();
                            raceCard.setId(rs1.getInt(1));
                            raceCard.setBetType(rs1.getString(2));
                            raceCard.setAtgTrackId(rs1.getInt(3));
                            raceCard.setAtgTrackCode(rs1.getString(4));
                            raceCard.setRaceDayDate(rs1.getDate(5).toLocalDate());
                            raceCards.add(raceCard);

                            ps2.setInt(1, raceCard.getId());

                            try (ResultSet rs2 = ps2.executeQuery()) {

                                while (rs2 != null && rs2.next()) {

                                    Leg leg = new Leg();
                                    leg.setId(rs2.getInt(1));
                                    leg.setRaceId(rs2.getInt(2));
                                    leg.setRaceCardId(rs2.getInt(3));
                                    leg.setLegNumber(rs2.getInt(4));
                                    raceCard.getLegs().add(leg);

                                    ps3.setInt(1, leg.getId());

                                    try (ResultSet rs3 = ps3.executeQuery()) {

                                        while (rs3 != null && rs3.next()) {

                                            LegParticipant lp = new LegParticipant();
                                            lp.setLegId(leg.getId());
                                            lp.setParticipantId(rs3.getInt(1));
                                            lp.setStartNumber(rs3.getInt(2));
                                            leg.getLegParticipants().add(lp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }

        return raceCards;
    }
}
