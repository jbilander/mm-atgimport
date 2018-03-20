package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.db.DbUtil;
import com.creang.db.MiniConnectionPoolManager;
import com.creang.model.*;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class InsertParticipantService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();
    private final MiniConnectionPoolManager poolManager = connectionPoolHelper.getMiniConnectionPoolManager();

    public void insert(List<Race> races) {

        String prepStmnt1 = "insert into participant (RaceId, StartNumber, Distance, StartPosition, Scratched, DriverChanged, DriverColor, CardWeight, ConditionWeight, ParWeight1, ParWeight2, PlusNumberWeight) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        String prepStmnt2 = "insert into driver (Id, KeyId, FirstName, LastName, ShortName, Amateur, ApprenticeAmateur, ApprenticePro) values (?,?,?,?,?,?,?,?)";
        String prepStmnt3 = "insert into trainer (Id, KeyId, FirstName, LastName, ShortName, Amateur, ApprenticeAmateur, ApprenticePro) values (?,?,?,?,?,?,?,?)";
        String prepStmnt4 = "insert into horse (Id, KeyId, Name, Age, Gender, Sire, Dam, DamSire, HomeTrack, Color, BlinkersType, Rating, StartPoint) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String prepStmnt5 = "insert into pastperformance (HorseId, Distance, Monte, GallopRace, StartPosition, ForeShoes, HindShoes, TrackState, DriverShortName, Earning, FirstPrize, FormattedTime, Odds, FormattedResult, Scratched, ScratchedReason, RaceDate, RaceNumber, AtgTrackCode, StartNumber, RaceTime, Blinkers, BlinkersType, Rating, TrackSurface, Weight) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String prepStmnt6 = "insert into record (HorseId, RecordType, Place, Distance, AtgTrackCode, Winner, FormattedTime) values (?,?,?,?,?,?,?)";
        String prepStmnt7 = "insert into yearstat (HorseId, YearStatType, Earnings, First, Second, Third, NumberOfStarts, ShowPercentage, WinPercentage, Year) values (?,?,?,?,?,?,?,?,?,?)";
        String prepStmnt8 = "update race set PostTime = ?, RaceName = ?, LongDesc = ?, ShortDesc = ?, Distance = ?, StartMethod = ?, TrackSurface = ?, HasParticipants = ?, TrackState = ?, Monte = ?, Gallop = ?, Updated = ? where Id = ?";

        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = poolManager.getValidConnection(3);
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(prepStmnt1, Statement.RETURN_GENERATED_KEYS)) {
                try (PreparedStatement stmt2 = conn.prepareStatement(prepStmnt2)) {
                    try (PreparedStatement stmt3 = conn.prepareStatement(prepStmnt3)) {
                        try (PreparedStatement stmt4 = conn.prepareStatement(prepStmnt4)) {
                            try (PreparedStatement stmt5 = conn.prepareStatement(prepStmnt5)) {
                                try (PreparedStatement stmt6 = conn.prepareStatement(prepStmnt6)) {
                                    try (PreparedStatement stmt7 = conn.prepareStatement(prepStmnt7)) {
                                        try (PreparedStatement stmt8 = conn.prepareStatement(prepStmnt8)) {

                                            for (Race race : races) {

                                                if (race.isHasParticipants()) {

                                                    for (Participant participant : race.getParticipants()) {

                                                        stmt1.setInt(1, race.getId());
                                                        stmt1.setInt(2, participant.getStartNumber());
                                                        stmt1.setInt(3, participant.getDistance());
                                                        stmt1.setInt(4, participant.getStartPosition());
                                                        stmt1.setBoolean(5, participant.isScratched());
                                                        stmt1.setBoolean(6, participant.isDriverChanged());
                                                        stmt1.setString(7, participant.getDriverColor());
                                                        stmt1.setBigDecimal(8, participant.getCardWeight());
                                                        stmt1.setBigDecimal(9, participant.getConditionWeight());
                                                        stmt1.setBigDecimal(10, participant.getParWeight1());
                                                        stmt1.setBigDecimal(11, participant.getParWeight2());
                                                        stmt1.setBigDecimal(12, participant.getPlusNumberWeight());
                                                        stmt1.executeUpdate();

                                                        rs = stmt1.getGeneratedKeys();

                                                        if (rs != null && rs.next()) {

                                                            int id = rs.getInt(1);
                                                            participant.setId(id);
                                                            participant.getDriver().setId(id);
                                                            participant.getTrainer().setId(id);
                                                            participant.getHorse().setId(id);
                                                            participant.getHorse().getPastPerformances().forEach(pp -> pp.setHorseId(id));
                                                            participant.getHorse().getRecords().forEach(r -> r.setHorseId(id));
                                                            participant.getHorse().getYearStats().forEach(ys -> ys.setHorseId(id));

                                                            stmt2.setInt(1, participant.getDriver().getId());
                                                            stmt2.setInt(2, participant.getDriver().getKeyId());
                                                            stmt2.setString(3, participant.getDriver().getFirstName());
                                                            stmt2.setString(4, participant.getDriver().getLastName());
                                                            stmt2.setString(5, participant.getDriver().getShortName());
                                                            stmt2.setBoolean(6, participant.getDriver().isAmateur());
                                                            stmt2.setBoolean(7, participant.getDriver().isApprenticeAmateur());
                                                            stmt2.setBoolean(8, participant.getDriver().isApprenticePro());
                                                            stmt2.executeUpdate();

                                                            stmt3.setInt(1, participant.getTrainer().getId());
                                                            stmt3.setInt(2, participant.getTrainer().getKeyId());
                                                            stmt3.setString(3, participant.getTrainer().getFirstName());
                                                            stmt3.setString(4, participant.getTrainer().getLastName());
                                                            stmt3.setString(5, participant.getTrainer().getShortName());
                                                            stmt3.setBoolean(6, participant.getTrainer().isAmateur());
                                                            stmt3.setBoolean(7, participant.getTrainer().isApprenticeAmateur());
                                                            stmt3.setBoolean(8, participant.getTrainer().isApprenticePro());
                                                            stmt3.executeUpdate();

                                                            stmt4.setInt(1, participant.getHorse().getId());
                                                            stmt4.setInt(2, participant.getHorse().getKeyId());
                                                            stmt4.setString(3, participant.getHorse().getName());
                                                            stmt4.setInt(4, participant.getHorse().getAge());
                                                            stmt4.setString(5, participant.getHorse().getGender());
                                                            stmt4.setString(6, participant.getHorse().getSire());
                                                            stmt4.setString(7, participant.getHorse().getDam());
                                                            stmt4.setString(8, participant.getHorse().getDamSire());
                                                            stmt4.setString(9, participant.getHorse().getHomeTrack());
                                                            stmt4.setString(10, participant.getHorse().getColor());
                                                            stmt4.setString(11, participant.getHorse().getBlinkersType());
                                                            stmt4.setObject(12, participant.getHorse().getRating(), Types.INTEGER);
                                                            stmt4.setInt(13, participant.getHorse().getStartPoint());
                                                            stmt4.executeUpdate();

                                                            for (PastPerformance pp : participant.getHorse().getPastPerformances()) {
                                                                stmt5.setInt(1, pp.getHorseId());
                                                                stmt5.setObject(2, pp.getDistance(), Types.INTEGER);
                                                                stmt5.setBoolean(3, pp.isMonte());
                                                                stmt5.setBoolean(4, pp.isGallopRace());
                                                                stmt5.setObject(5, pp.getStartPosition(), Types.INTEGER);
                                                                stmt5.setObject(6, pp.getForeShoes(), Types.BOOLEAN);
                                                                stmt5.setObject(7, pp.getHindShoes(), Types.BOOLEAN);
                                                                stmt5.setString(8, pp.getTrackState());
                                                                stmt5.setString(9, pp.getDriverShortName());
                                                                stmt5.setBigDecimal(10, pp.getEarning());
                                                                stmt5.setBigDecimal(11, pp.getFirstPrize());
                                                                stmt5.setString(12, pp.getFormattedTime());
                                                                stmt5.setString(13, pp.getOdds());
                                                                stmt5.setString(14, pp.getFormattedResult());
                                                                stmt5.setBoolean(15, pp.isScratched());
                                                                stmt5.setString(16, pp.getScratchedReason());
                                                                stmt5.setDate(17, Date.valueOf(pp.getRaceDate()));
                                                                stmt5.setObject(18, pp.getRaceNumber(), Types.INTEGER);
                                                                stmt5.setString(19, pp.getAtgTrackCode());
                                                                stmt5.setObject(20, pp.getStartNumber(), Types.INTEGER);
                                                                stmt5.setString(21, pp.getRaceTime());
                                                                stmt5.setBoolean(22, pp.isBlinkers());
                                                                stmt5.setString(23, pp.getBlinkersType());
                                                                stmt5.setInt(24, pp.getRating());
                                                                stmt5.setString(25, pp.getTrackSurface());
                                                                stmt5.setBigDecimal(26, pp.getWeight());
                                                                stmt5.addBatch();
                                                            }
                                                            stmt5.executeBatch();

                                                            for (Record record : participant.getHorse().getRecords()) {
                                                                stmt6.setInt(1, record.getHorseId());
                                                                stmt6.setString(2, record.getRecordType());
                                                                stmt6.setInt(3, record.getPlace());
                                                                stmt6.setInt(4, record.getDistance());
                                                                stmt6.setString(5, record.getAtgTrackCode());
                                                                stmt6.setBoolean(6, record.isWinner());
                                                                stmt6.setString(7, record.getFormattedTime());
                                                                stmt6.addBatch();
                                                            }
                                                            stmt6.executeBatch();

                                                            for (YearStat yearStat : participant.getHorse().getYearStats()) {
                                                                stmt7.setInt(1, yearStat.getHorseId());
                                                                stmt7.setInt(2, yearStat.getYearStatType());
                                                                stmt7.setBigDecimal(3, yearStat.getEarnings());
                                                                stmt7.setInt(4, yearStat.getFirst());
                                                                stmt7.setInt(5, yearStat.getSecond());
                                                                stmt7.setInt(6, yearStat.getThird());
                                                                stmt7.setInt(7, yearStat.getNumberOfStarts());
                                                                stmt7.setBigDecimal(8, yearStat.getShowPercentage());
                                                                stmt7.setBigDecimal(9, yearStat.getWinPercentage());
                                                                stmt7.setInt(10, yearStat.getYear());
                                                                stmt7.addBatch();
                                                            }
                                                            stmt7.executeBatch();

                                                            stmt8.setTime(1, Time.valueOf(race.getPostTime()));
                                                            stmt8.setString(2, race.getRaceName());
                                                            stmt8.setString(3, race.getLongDesc());
                                                            stmt8.setString(4, race.getShortDesc());
                                                            stmt8.setInt(5, race.getDistance());
                                                            stmt8.setString(6, race.getStartMethod());
                                                            stmt8.setString(7, race.getTrackSurface());
                                                            stmt8.setBoolean(8, race.isHasParticipants());
                                                            stmt8.setString(9, race.getTrackState());
                                                            stmt8.setBoolean(10, race.isMonte());
                                                            stmt8.setBoolean(11, race.isGallop());
                                                            stmt8.setString(12, Util.getCurrentLocalDateTimeString());
                                                            stmt8.setInt(13, race.getId());
                                                            stmt8.executeUpdate();
                                                        }
                                                    }
                                                }
                                            }
                                            conn.commit();
                                        }
                                    }
                                }
                            }
                        }
                    }
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
