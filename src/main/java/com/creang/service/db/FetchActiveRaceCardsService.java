package com.creang.service.db;

import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;
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
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public List<RaceCard> fetch(LocalDate fromDate, LocalDate toDate) {

        List<RaceCard> raceCards = new ArrayList<>();

        Connection conn = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        PreparedStatement prepStmnt1 = null;
        PreparedStatement prepStmnt2 = null;
        PreparedStatement prepStmnt3 = null;

        try {

            conn = poolManager.getValidConnection(3);

            prepStmnt1 = conn.prepareStatement("select Id, BetType, AtgTrackId, AtgTrackCode, RaceDayDate from racecard where RaceDayDate between ? and ? and Activated = 1 and HasCompleteResult = 0 order by RaceDayDate, AtgTrackId, AtgTrackCode");
            prepStmnt2 = conn.prepareStatement("select Id, RaceId, RaceCardId, LegNumber from leg where RaceCardId = ? order by LegNumber");
            prepStmnt3 = conn.prepareStatement("select lp.ParticipantId, p.StartNumber from legparticipant lp inner join participant p on lp.ParticipantId = p.Id where lp.LegId = ? order by StartNumber");

            prepStmnt1.setDate(1, Date.valueOf(fromDate));
            prepStmnt1.setDate(2, Date.valueOf(toDate));
            rs1 = prepStmnt1.executeQuery();

            while (rs1 != null && rs1.next()) {

                RaceCard raceCard = new RaceCard();
                raceCard.setId(rs1.getInt(1));
                raceCard.setBetType(rs1.getString(2));
                raceCard.setAtgTrackId(rs1.getInt(3));
                raceCard.setAtgTrackCode(rs1.getString(4));
                raceCard.setRaceDayDate(rs1.getDate(5).toLocalDate());
                raceCards.add(raceCard);

                prepStmnt2.setInt(1, raceCard.getId());
                rs2 = prepStmnt2.executeQuery();

                while (rs2 != null && rs2.next()) {
                    Leg leg = new Leg();
                    leg.setId(rs2.getInt(1));
                    leg.setRaceId(rs2.getInt(2));
                    leg.setRaceCardId(rs2.getInt(3));
                    leg.setLegNumber(rs2.getInt(4));
                    raceCard.getLegs().add(leg);

                    prepStmnt3.setInt(1, leg.getId());
                    rs3 = prepStmnt3.executeQuery();

                    while (rs3 != null && rs3.next()) {
                        LegParticipant lp = new LegParticipant();
                        lp.setLegId(leg.getId());
                        lp.setParticipantId(rs3.getInt(1));
                        lp.setStartNumber(rs3.getInt(2));
                        leg.getLegParticipants().add(lp);
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            DbUtil.closeResultSet(rs1);
            DbUtil.closeResultSet(rs2);
            DbUtil.closeResultSet(rs3);
            DbUtil.closeStatement(prepStmnt1);
            DbUtil.closeStatement(prepStmnt2);
            DbUtil.closeStatement(prepStmnt3);
            poolManager.release(conn);
        }

        return raceCards;
    }
}
