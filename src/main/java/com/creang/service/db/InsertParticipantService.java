package com.creang.service.db;

import com.creang.common.Util;
import com.creang.db.ConnectionPoolHelper;
import com.creang.model.*;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class InsertParticipantService {

    private final Logger logger = loggerUtil.getLogger();
    private final ConnectionPoolHelper connectionPoolHelper = ConnectionPoolHelper.getInstance();

    public void insert(List<Race> races) {

        String sql1 = "insert into participant (RaceId, StartNumber, Distance, StartPosition, Scratched, DriverChanged, DriverColor, CardWeight, ConditionWeight, ParWeight1, ParWeight2, PlusNumberWeight) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql2 = "insert into driver (Id, KeyId, FirstName, LastName, ShortName, Amateur, ApprenticeAmateur, ApprenticePro) values (?,?,?,?,?,?,?,?)";
        String sql3 = "insert into trainer (Id, KeyId, FirstName, LastName, ShortName, Amateur, ApprenticeAmateur, ApprenticePro) values (?,?,?,?,?,?,?,?)";
        String sql4 = "insert into horse (Id, KeyId, Name, Age, Gender, Sire, Dam, DamSire, HomeTrack, Color, BlinkersType, Rating) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql5 = "insert into pastperformance (HorseId, Distance, Monte, GallopRace, StartPosition, ForeShoes, HindShoes, TrackState, DriverShortName, Earning, FirstPrize, FormattedTime, Odds, FormattedResult, Scratched, ScratchedReason, RaceDate, RaceNumber, AtgTrackCode, StartNumber, RaceTime, Blinkers, BlinkersType, Rating, TrackSurface, Weight) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql6 = "insert into record (HorseId, RecordType, Place, Distance, AtgTrackCode, Winner, FormattedTime) values (?,?,?,?,?,?,?)";
        String sql7 = "insert into yearstat (HorseId, YearStatType, Earnings, First, Second, Third, NumberOfStarts, ShowPercentage, WinPercentage, Year) values (?,?,?,?,?,?,?,?,?,?)";
        String sql8 = "update race set PostTime = ?, RaceName = ?, LongDesc = ?, ShortDesc = ?, Distance = ?, StartMethod = ?, TrackSurface = ?, HasParticipants = ?, TrackState = ?, Monte = ?, Gallop = ?, Updated = ? where Id = ?";

        try (Connection conn = connectionPoolHelper.getDataSource().getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {
                try (PreparedStatement ps2 = conn.prepareStatement(sql2)) {
                    try (PreparedStatement ps3 = conn.prepareStatement(sql3)) {
                        try (PreparedStatement ps4 = conn.prepareStatement(sql4)) {
                            try (PreparedStatement ps5 = conn.prepareStatement(sql5)) {
                                try (PreparedStatement ps6 = conn.prepareStatement(sql6)) {
                                    try (PreparedStatement ps7 = conn.prepareStatement(sql7)) {
                                        try (PreparedStatement ps8 = conn.prepareStatement(sql8)) {

                                            for (Race race : races) {

                                                if (race.isHasParticipants()) {

                                                    for (Participant participant : race.getParticipants()) {

                                                        ps1.setInt(1, race.getId());
                                                        ps1.setInt(2, participant.getStartNumber());
                                                        ps1.setInt(3, participant.getDistance());
                                                        ps1.setInt(4, participant.getStartPosition());
                                                        ps1.setBoolean(5, participant.isScratched());
                                                        ps1.setBoolean(6, participant.isDriverChanged());
                                                        ps1.setString(7, participant.getDriverColor());
                                                        ps1.setBigDecimal(8, participant.getCardWeight());
                                                        ps1.setBigDecimal(9, participant.getConditionWeight());
                                                        ps1.setBigDecimal(10, participant.getParWeight1());
                                                        ps1.setBigDecimal(11, participant.getParWeight2());
                                                        ps1.setBigDecimal(12, participant.getPlusNumberWeight());
                                                        ps1.executeUpdate();

                                                        try (ResultSet rs = ps1.getGeneratedKeys()) {

                                                            if (rs != null && rs.next()) {

                                                                int id = rs.getInt(1);

                                                                participant.setId(id);
                                                                participant.getDriver().setId(id);
                                                                participant.getTrainer().setId(id);
                                                                participant.getHorse().setId(id);
                                                                participant.getHorse().getPastPerformances().forEach(pp -> pp.setHorseId(id));
                                                                participant.getHorse().getRecords().forEach(r -> r.setHorseId(id));
                                                                participant.getHorse().getYearStats().forEach(ys -> ys.setHorseId(id));

                                                                ps2.setInt(1, participant.getDriver().getId());
                                                                ps2.setInt(2, participant.getDriver().getKeyId());
                                                                ps2.setString(3, participant.getDriver().getFirstName());
                                                                ps2.setString(4, participant.getDriver().getLastName());
                                                                ps2.setString(5, participant.getDriver().getShortName());
                                                                ps2.setBoolean(6, participant.getDriver().isAmateur());
                                                                ps2.setBoolean(7, participant.getDriver().isApprenticeAmateur());
                                                                ps2.setBoolean(8, participant.getDriver().isApprenticePro());
                                                                ps2.executeUpdate();

                                                                ps3.setInt(1, participant.getTrainer().getId());
                                                                ps3.setInt(2, participant.getTrainer().getKeyId());
                                                                ps3.setString(3, participant.getTrainer().getFirstName());
                                                                ps3.setString(4, participant.getTrainer().getLastName());
                                                                ps3.setString(5, participant.getTrainer().getShortName());
                                                                ps3.setBoolean(6, participant.getTrainer().isAmateur());
                                                                ps3.setBoolean(7, participant.getTrainer().isApprenticeAmateur());
                                                                ps3.setBoolean(8, participant.getTrainer().isApprenticePro());
                                                                ps3.executeUpdate();

                                                                ps4.setInt(1, participant.getHorse().getId());
                                                                ps4.setInt(2, participant.getHorse().getKeyId());
                                                                ps4.setString(3, participant.getHorse().getName());
                                                                ps4.setInt(4, participant.getHorse().getAge());
                                                                ps4.setString(5, participant.getHorse().getGender());
                                                                ps4.setString(6, participant.getHorse().getSire());
                                                                ps4.setString(7, participant.getHorse().getDam());
                                                                ps4.setString(8, participant.getHorse().getDamSire());
                                                                ps4.setString(9, participant.getHorse().getHomeTrack());
                                                                ps4.setString(10, participant.getHorse().getColor());
                                                                ps4.setString(11, participant.getHorse().getBlinkersType());
                                                                ps4.setObject(12, participant.getHorse().getRating(), Types.INTEGER);
                                                                ps4.executeUpdate();

                                                                for (PastPerformance pp : participant.getHorse().getPastPerformances()) {

                                                                    ps5.setInt(1, pp.getHorseId());
                                                                    ps5.setObject(2, pp.getDistance(), Types.INTEGER);
                                                                    ps5.setBoolean(3, pp.isMonte());
                                                                    ps5.setBoolean(4, pp.isGallopRace());
                                                                    ps5.setObject(5, pp.getStartPosition(), Types.INTEGER);
                                                                    ps5.setObject(6, pp.getForeShoes(), Types.BOOLEAN);
                                                                    ps5.setObject(7, pp.getHindShoes(), Types.BOOLEAN);
                                                                    ps5.setString(8, pp.getTrackState());
                                                                    ps5.setString(9, pp.getDriverShortName());
                                                                    ps5.setBigDecimal(10, pp.getEarning());
                                                                    ps5.setBigDecimal(11, pp.getFirstPrize());
                                                                    ps5.setString(12, pp.getFormattedTime());
                                                                    ps5.setString(13, pp.getOdds());
                                                                    ps5.setString(14, pp.getFormattedResult());
                                                                    ps5.setBoolean(15, pp.isScratched());
                                                                    ps5.setString(16, pp.getScratchedReason());
                                                                    ps5.setDate(17, Date.valueOf(pp.getRaceDate()));
                                                                    ps5.setObject(18, pp.getRaceNumber(), Types.INTEGER);
                                                                    ps5.setString(19, pp.getAtgTrackCode());
                                                                    ps5.setObject(20, pp.getStartNumber(), Types.INTEGER);
                                                                    ps5.setString(21, pp.getRaceTime());
                                                                    ps5.setBoolean(22, pp.isBlinkers());
                                                                    ps5.setString(23, pp.getBlinkersType());
                                                                    ps5.setInt(24, pp.getRating());
                                                                    ps5.setString(25, pp.getTrackSurface());
                                                                    ps5.setBigDecimal(26, pp.getWeight());
                                                                    ps5.addBatch();
                                                                }
                                                                ps5.executeBatch();

                                                                for (Record record : participant.getHorse().getRecords()) {

                                                                    ps6.setInt(1, record.getHorseId());
                                                                    ps6.setString(2, record.getRecordType());
                                                                    ps6.setInt(3, record.getPlace());
                                                                    ps6.setInt(4, record.getDistance());
                                                                    ps6.setString(5, record.getAtgTrackCode());
                                                                    ps6.setBoolean(6, record.isWinner());
                                                                    ps6.setString(7, record.getFormattedTime());
                                                                    ps6.addBatch();
                                                                }
                                                                ps6.executeBatch();

                                                                for (YearStat yearStat : participant.getHorse().getYearStats()) {

                                                                    ps7.setInt(1, yearStat.getHorseId());
                                                                    ps7.setInt(2, yearStat.getYearStatType());
                                                                    ps7.setBigDecimal(3, yearStat.getEarnings());
                                                                    ps7.setInt(4, yearStat.getFirst());
                                                                    ps7.setInt(5, yearStat.getSecond());
                                                                    ps7.setInt(6, yearStat.getThird());
                                                                    ps7.setInt(7, yearStat.getNumberOfStarts());
                                                                    ps7.setBigDecimal(8, yearStat.getShowPercentage());
                                                                    ps7.setBigDecimal(9, yearStat.getWinPercentage());
                                                                    ps7.setInt(10, yearStat.getYear());
                                                                    ps7.addBatch();
                                                                }
                                                                ps7.executeBatch();

                                                                ps8.setTime(1, Time.valueOf(race.getPostTime()));
                                                                ps8.setString(2, race.getRaceName());
                                                                ps8.setString(3, race.getLongDesc());
                                                                ps8.setString(4, race.getShortDesc());
                                                                ps8.setInt(5, race.getDistance());
                                                                ps8.setString(6, race.getStartMethod());
                                                                ps8.setString(7, race.getTrackSurface());
                                                                ps8.setBoolean(8, race.isHasParticipants());
                                                                ps8.setString(9, race.getTrackState());
                                                                ps8.setBoolean(10, race.isMonte());
                                                                ps8.setBoolean(11, race.isGallop());
                                                                ps8.setString(12, Util.getCurrentLocalDateTimeString());
                                                                ps8.setInt(13, race.getId());
                                                                ps8.executeUpdate();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
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
