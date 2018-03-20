package com.creang.task;

import aisbean.RacingCard;
import aisbean.ResultRow;
import aisbean.Start;
import aisbean.Weight;
import com.creang.common.Util;
import com.creang.common.YearStatType;
import com.creang.model.*;
import com.creang.service.AtgPartnerInfoService;
import com.creang.service.db.FetchInactiveRacesService;
import com.creang.service.db.InsertParticipantService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class RacingCardTask {

    private final Logger logger = loggerUtil.getLogger();
    private final AtgPartnerInfoService atgPartnerInfoService = new AtgPartnerInfoService();
    private final FetchInactiveRacesService fetchInactiveRacesService = new FetchInactiveRacesService();
    private final InsertParticipantService insertParticipantService = new InsertParticipantService();

    public void run() {

        List<Race> races = fetchInactiveRacesService.fetch(LocalDate.now());

        for (Race race : races) {

            try {

                logger.info("Start: fetchRacingCardRace");
                RacingCard racingCard = atgPartnerInfoService.fetchRacingCardRace(race.getRaceDayDate(), race.getAtgTrackId(), race.getRaceNumber());
                logger.info("Done: fetchRacingCardRace");
                logger.info("Start: processRacingCard");

                if (racingCard.getRaces() != null && racingCard.getRaces().getRace().size() == 1) {

                    aisbean.Race atgRace = racingCard.getRaces().getRace().get(0);

                    if (atgRace.getStarts() != null && atgRace.getStarts().getStart().stream().anyMatch(s -> !s.isOutsideTote())) {
                        long numberOfStarts = atgRace.getStarts().getStart().stream().filter(s -> !s.isOutsideTote()).count();
                        processRacingCard(race, atgRace);

                        if (race.getParticipants().size() == numberOfStarts && race.getParticipants().stream().allMatch(p -> p.getHorse() != null && p.getDriver() != null && p.getTrainer() != null && (p.getDistance() > 0 || p.getCardWeight() != null))) {
                            race.setHasParticipants(true);
                        }
                    }
                }
                logger.info("Done: processRacingCard");

            } catch (Exception e) {
                logger.severe("Error when calling fetchRacingCardRace(): " + e.getMessage());
            }
        }

        if (races.size() > 0) {
            insertParticipantService.insert(races);
        }
    }

    private void processRacingCard(Race race, aisbean.Race atgRace) {

        if (atgRace.getConditions() != null) {
            race.setRaceName(atgRace.getConditions().getRaceName());
            race.setShortDesc(atgRace.getConditions().getTextShort());
            race.setLongDesc(atgRace.getConditions().getTextLong());
        }

        if (atgRace.getDistance() != null) {
            race.setDistance(Integer.parseInt(atgRace.getDistance()));
        }

        race.setPostTime(Util.getLocalTimeFromAtgTime(atgRace.getPostTime()));

        if (atgRace.getTrotRaceInfo() != null) {
            race.setMonte(atgRace.getTrotRaceInfo().isMonte());
            if (atgRace.getTrotRaceInfo().getStartMethod() != null) {
                race.setStartMethod(atgRace.getTrotRaceInfo().getStartMethod().getCode());
            }
        }

        if (atgRace.getGallopRaceInfo() != null) {
            race.setGallop(true);
            if (atgRace.getGallopRaceInfo().getTrackSurface() != null) {
                race.setTrackSurface(atgRace.getGallopRaceInfo().getTrackSurface().getCode());
            }
        }

        if (atgRace.getTrackState() != null) {
            race.setTrackState(atgRace.getTrackState().getCode());
        }

        //region process Starts
        for (Start start : atgRace.getStarts().getStart()) {

            if (!start.isOutsideTote()) {

                Participant participant = new Participant();
                participant.setRaceId(race.getId());
                participant.setStartNumber(start.getStartNr());
                participant.setDriverChanged(start.isDriverChanged());
                participant.setDriverColor(start.getDriverColour());

                if (start.getHorse() != null) {

                    aisbean.Horse atgHorse = start.getHorse();
                    Horse horse = new Horse();

                    if (atgHorse.getKey() != null) {
                        horse.setKeyId(atgHorse.getKey().getId());
                    }

                    horse.setName(atgHorse.getHorseNameAndNationality());
                    horse.setAge(atgHorse.getAge());
                    horse.setStartPoint(start.getStartPoint());

                    if (atgHorse.getSex() != null) {
                        horse.setGender(atgHorse.getSex().getCode());
                    }
                    if (atgHorse.getSire() != null) {
                        horse.setSire(atgHorse.getSire().getName());
                    }
                    if (atgHorse.getDam() != null) {
                        horse.setDam(atgHorse.getDam().getName());
                    }
                    if (atgHorse.getDamSire() != null) {
                        horse.setDamSire(atgHorse.getDamSire().getName());
                    }
                    if (atgHorse.getColour() != null) {
                        horse.setColor(atgHorse.getColour().getCode());
                    }
                    participant.setHorse(horse);
                }

                if (start.getTrotStartInfo() != null) {

                    participant.setStartPosition(start.getTrotStartInfo().getPostPosition());

                    if (start.getTrotStartInfo().getDistance() != null) {
                        participant.setDistance(Integer.parseInt(start.getTrotStartInfo().getDistance()));
                    }

                    if (start.getTrotStartInfo().getHomeTrack() != null && participant.getHorse() != null) {
                        participant.getHorse().setHomeTrack(start.getTrotStartInfo().getHomeTrack().getCode());
                    }
                    if (participant.getHorse() != null && start.getTrotStartInfo().getRecords() != null && start.getTrotStartInfo().getRecords().getRecord().size() > 0) {
                        processRecords(participant.getHorse(), start.getTrotStartInfo().getRecords().getRecord());
                    }
                }

                if (start.getGallopStartInfo() != null) {
                    if (start.getGallopStartInfo().getWeight() != null) {
                        Weight weight = start.getGallopStartInfo().getWeight();
                        participant.setCardWeight(BigDecimal.valueOf(weight.getCardWeight()));
                        participant.setConditionWeight(BigDecimal.valueOf(weight.getConditionWeight()));
                        participant.setParWeight1(BigDecimal.valueOf(weight.getParVikt1()));
                        participant.setParWeight2(BigDecimal.valueOf(weight.getParVikt2()));
                        participant.setPlusNumberWeight(BigDecimal.valueOf(weight.getPlustalsVikt()));
                    }
                    if (start.getGallopStartInfo().getBlinkersType() != null && participant.getHorse() != null) {
                        participant.getHorse().setBlinkersType(start.getGallopStartInfo().getBlinkersType());
                    }
                    if (start.getGallopStartInfo().getRating() != null && participant.getHorse() != null) {
                        participant.getHorse().setRating(start.getGallopStartInfo().getRating());
                    }
                }

                if (start.getDriver() != null) {
                    Driver driver = new Driver();
                    driver.setKeyId(start.getDriver().getId());
                    driver.setFirstName(start.getDriver().getName().trim());
                    driver.setLastName(start.getDriver().getSurName().trim());
                    driver.setShortName(start.getDriver().getShortName().trim());
                    driver.setAmateur(start.getDriver().isAmateur());
                    driver.setApprenticeAmateur(start.getDriver().isApprenticeAmateur());
                    driver.setApprenticePro(start.getDriver().isApprenticePro());
                    participant.setDriver(driver);
                }
                if (start.getTrainer() != null) {
                    Trainer trainer = new Trainer();
                    trainer.setKeyId(start.getTrainer().getId());
                    trainer.setFirstName(start.getTrainer().getName().trim());
                    trainer.setLastName(start.getTrainer().getSurName().trim());
                    trainer.setShortName(start.getTrainer().getShortName().trim());
                    trainer.setAmateur(start.getTrainer().isAmateur());
                    trainer.setApprenticeAmateur(start.getTrainer().isApprenticeAmateur());
                    trainer.setApprenticePro(start.getTrainer().isApprenticePro());
                    participant.setTrainer(trainer);
                }

                if (participant.getHorse() != null && start.getHorseStat() != null) {

                    if (start.getHorseStat().getCurrent() != null) {
                        processYearStat(participant.getHorse(), start.getHorseStat().getCurrent(), YearStatType.current);
                    }
                    if (start.getHorseStat().getPrevious() != null) {
                        processYearStat(participant.getHorse(), start.getHorseStat().getPrevious(), YearStatType.previous);
                    }
                    if (start.getHorseStat().getTotal() != null) {
                        processYearStat(participant.getHorse(), start.getHorseStat().getTotal(), YearStatType.total);
                    }
                    if (start.getHorseStat().getTotalCurrentDistance() != null) {
                        processYearStat(participant.getHorse(), start.getHorseStat().getTotalCurrentDistance(), YearStatType.currentDistance);
                    }
                    if (start.getHorseStat().getTotalSlowTrackState() != null) {
                        processYearStat(participant.getHorse(), start.getHorseStat().getTotalSlowTrackState(), YearStatType.slowTrack);
                    }

                    if (start.getHorseStat().getPastPerformances() != null && start.getHorseStat().getPastPerformances().getResultRow().size() > 0) {
                        processResultRows(participant.getHorse(), start.getHorseStat().getPastPerformances().getResultRow());
                    }
                }
                race.getParticipants().add(participant);
            }
        }
        //endregion process Starts
    }

    private void processResultRows(Horse horse, List<ResultRow> resultRows) {

        for (ResultRow resultRow : resultRows) {

            PastPerformance pp = new PastPerformance();

            pp.setStartNumber(resultRow.getStartNr());
            pp.setFormattedTime(resultRow.getFormattedTime());
            pp.setOdds(resultRow.getOdds());

            if (resultRow.getCircumstances() != null) {
                pp.setDistance(resultRow.getCircumstances().getDistance());
                pp.setMonte(resultRow.getCircumstances().isMonte());
                pp.setStartPosition(resultRow.getCircumstances().getPostPosition());
                if (resultRow.getCircumstances().getShoeInfo() != null) {
                    pp.setForeShoes(resultRow.getCircumstances().getShoeInfo().isForeshoes());
                    pp.setHindShoes(resultRow.getCircumstances().getShoeInfo().isHindshoes());
                }
                if (resultRow.getCircumstances().getTrackState() != null) {
                    pp.setTrackState(resultRow.getCircumstances().getTrackState().getCode().toLowerCase());
                }
            }
            if (resultRow.getDriver() != null) {
                pp.setDriverShortName(resultRow.getDriver().getShortName());
            }
            if (resultRow.getEarning() != null) {
                pp.setEarning(resultRow.getEarning().getSum());
            }
            if (resultRow.getFirstPrize() != null) {
                pp.setFirstPrize(resultRow.getFirstPrize().getSum());
            }
            if (resultRow.getGallopInfo() != null) {
                pp.setGallopRace(true);
                pp.setBlinkers(resultRow.getGallopInfo().isBlinkers());
                pp.setBlinkersType(resultRow.getGallopInfo().getBlinkersType());
                pp.setRating(resultRow.getGallopInfo().getRating());
                pp.setWeight(BigDecimal.valueOf(resultRow.getGallopInfo().getWeight()));
                if (resultRow.getGallopInfo().getTrackSurface() != null) {
                    pp.setTrackSurface(resultRow.getGallopInfo().getTrackSurface().getCode());
                }
            }

            if (resultRow.getPlaceInfo() != null) {
                pp.setFormattedResult(resultRow.getPlaceInfo().getFormattedResult());
                pp.setScratched(resultRow.getPlaceInfo().isScratched());
                pp.setScratchedReason(resultRow.getPlaceInfo().getScratchedReason());
            }

            if (resultRow.getRaceKey() != null) {

                pp.setRaceNumber(resultRow.getRaceKey().getRaceNr());

                if (resultRow.getRaceKey().getDate() != null) {
                    int year = resultRow.getRaceKey().getDate().getYear();
                    int month = resultRow.getRaceKey().getDate().getMonth();
                    int dayOfMonth = resultRow.getRaceKey().getDate().getDate();
                    pp.setRaceDate(LocalDate.of(year, month, dayOfMonth));
                }

                if (resultRow.getRaceKey().getTrack() != null) {
                    pp.setAtgTrackCode(resultRow.getRaceKey().getTrack().getCode());
                }
            }

            if (resultRow.getTotalTime() != null && resultRow.getTotalTime().getTime() != null) {
                String formattedTime = Util.getKmTimefromAtgTime(resultRow.getTotalTime().getTime());
                if (!formattedTime.equals("0:00,0")) {
                    pp.setRaceTime(formattedTime);
                }
            }
            horse.getPastPerformances().add(pp);
        }
    }

    private void processYearStat(Horse horse, aisbean.YearStat yearStat, int yearStatType) {

        YearStat ys = new YearStat();
        ys.setYearStatType(yearStatType);
        ys.setFirst(yearStat.getFirst());
        ys.setSecond(yearStat.getSecond());
        ys.setThird(yearStat.getThird());
        ys.setNumberOfStarts(yearStat.getNrOfStarts());
        ys.setWinPercentage(new BigDecimal(yearStat.getPercentWin()));
        ys.setShowPercentage(new BigDecimal(yearStat.getPercent123()));

        if (yearStat.getYear() != null) {
            ys.setYear(yearStat.getYear().getYear());
        }
        if (yearStat.getEarning() != null) {
            ys.setEarnings(yearStat.getEarning().getSum());
        }
        horse.getYearStats().add(ys);
    }

    private void processRecords(Horse horse, List<aisbean.Record> records) {

        for (aisbean.Record atgRecord : records) {

            Record record = new Record();

            if (atgRecord.getRecType() != null) {
                record.setRecordType(atgRecord.getRecType().getCode());
            }
            record.setPlace(atgRecord.getPlace());
            record.setDistance(atgRecord.getDistance());
            record.setAtgTrackCode(atgRecord.getTrack().getCode());
            record.setWinner(atgRecord.isWinner());
            String second = atgRecord.getTime().getSecond() < 10 ? "0" + atgRecord.getTime().getSecond() : "" + atgRecord.getTime().getSecond();
            record.setFormattedTime(second + "," + atgRecord.getTime().getTenth());
            horse.getRecords().add(record);
        }
    }
}
