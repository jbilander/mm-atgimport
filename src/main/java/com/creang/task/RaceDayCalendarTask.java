package com.creang.task;

import aisbean.*;
import com.creang.common.Util;
import com.creang.model.Leg;
import com.creang.model.Race;
import com.creang.model.RaceCard;
import com.creang.service.AtgPartnerInfoService;
import com.creang.service.db.FetchRaceIdService;
import com.creang.service.db.InsertRaceCardService;
import com.creang.service.db.InsertRaceService;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.creang.Main.loggerUtil;

public class RaceDayCalendarTask {

    private final Logger logger = loggerUtil.getLogger();
    private final AtgPartnerInfoService atgPartnerInfoService = new AtgPartnerInfoService();
    private final InsertRaceService insertRaceService = new InsertRaceService();
    private final FetchRaceIdService fetchRaceIdService = new FetchRaceIdService();
    private final InsertRaceCardService insertRaceCardService = new InsertRaceCardService();

    public void run() {

        try {
            logger.info("Start: fetchRaceDayCalendar");
            RaceDayCalendar raceDayCalendar = atgPartnerInfoService.fetchRaceDayCalendar();
            logger.info("Done: fetchRaceDayCalendar");
            logger.info("Start: processRaceDayCalendar");
            processRaceDayCalendar(raceDayCalendar);
            logger.info("Done: processRaceDayCalendar");

        } catch (Exception e) {
            logger.severe("Error when calling fetchRaceDayCalendar(): " + e.getMessage());
        }
    }

    private void processRaceDayCalendar(RaceDayCalendar raceDayCalendar) {

        if (raceDayCalendar.getRaceDayInfos() != null) {

            LocalDate yesterday = LocalDate.now().minusDays(1);

            Set<Race> races = new LinkedHashSet<>();
            addRaces(raceDayCalendar.getRaceDayInfos(), races, yesterday);

            if (races.size() > 0) {
                insertRaceService.insert(races);
            }

            Set<RaceCard> raceCards = new LinkedHashSet<>();
            addSingleTrackRaceCards(raceDayCalendar.getRaceDayInfos(), raceCards, yesterday);

            if (raceDayCalendar.getMultipleTrackPoolSetups() != null) {
                addMultiTrackRaceCards(raceDayCalendar.getMultipleTrackPoolSetups(), raceCards, yesterday);
            }

            if (raceCards.size() > 0) {
                insertRaceCardService.insert(raceCards);
            }
        }
    }

    private void addRaces(ArrayOfRaceDayInfo raceDayInfos, Set<Race> races, LocalDate yesterday) {

        for (RaceDayInfo rdi : raceDayInfos.getRaceDayInfo()) {

            LocalDate raceDayDate = Util.getLocalDateFromAtgDate(rdi.getRaceDayDate());

            //add only races from today and forward with available racecards
            if (raceDayDate.isAfter(yesterday) && rdi.getRaceInfos() != null && rdi.isRaceCardAvailable()) {

                for (RaceInfo raceInfo : rdi.getRaceInfos().getRaceInfo()) {

                    if (raceInfo.getBetTypeCodes().getString().stream().anyMatch(c ->
                            c.equals("V3") ||
                                    c.equals("V4") ||
                                    c.equals("V5") ||
                                    c.equals("V64") ||
                                    c.equals("V65") ||
                                    c.equals("V75") ||
                                    c.equals("GS75") ||
                                    c.equals("V86"))) {

                        Race race = new Race();
                        race.setAtgTrackId(rdi.getTrackKey().getTrackId());
                        race.setAtgTrackCode(rdi.getTrack().getCode());
                        race.setTrackName(rdi.getTrack().getDomesticText());
                        race.setRaceNumber(raceInfo.getRaceNr());
                        race.setPostTime(Util.getLocalTimeFromAtgTime(raceInfo.getPostTime()));
                        race.setRaceDayDate(Util.getLocalDateFromAtgDate(rdi.getRaceDayDate()));
                        races.add(race);
                    }
                }
            }
        }
    }

    private void addSingleTrackRaceCards(ArrayOfRaceDayInfo raceDayInfos, Set<RaceCard> raceCards, LocalDate yesterday) {

        for (RaceDayInfo rdi : raceDayInfos.getRaceDayInfo()) {

            LocalDate raceDayDate = Util.getLocalDateFromAtgDate(rdi.getRaceDayDate());

            //add only racecards from today and forward with available racecards
            if (raceDayDate.isAfter(yesterday) && rdi.getRaceInfos() != null && rdi.isRaceCardAvailable()) {

                for (BetType betType : rdi.getBetTypes().getBetType()) {

                    int numberOfLegs = Util.getNumberOfLegs(betType.getName().getCode());
                    String betTypeCode = betType.getName().getCode();

                    if ((betTypeCode.equals("V3") ||
                            betTypeCode.equals("V4") ||
                            betTypeCode.equals("V5") ||
                            betTypeCode.equals("V64") ||
                            betTypeCode.equals("V65") ||
                            betTypeCode.equals("V75") ||
                            betTypeCode.equals("GS75") ||
                            betTypeCode.equals("V86")) && betType.getRaces().getInt().size() == numberOfLegs) {

                        RaceCard raceCard = new RaceCard();
                        raceCard.setAtgTrackId(rdi.getTrackKey().getTrackId());
                        raceCard.setAtgTrackCode(rdi.getTrack().getCode());
                        raceCard.setBetType(betTypeCode);
                        raceCard.setRaceDayDate(raceDayDate);
                        raceCard.setTrackName(rdi.getTrack().getDomesticText());
                        raceCard.setHasBoost(betType.isHasBoost());

                        //add legs

                        for (int legNumber = 1; legNumber <= betType.getRaces().getInt().size(); legNumber++) {

                            Integer raceId = fetchRaceIdService.fetch(raceDayDate, raceCard.getAtgTrackId(), raceCard.getAtgTrackCode(), betType.getRaces().getInt().get(legNumber - 1));

                            if (raceId != null) {

                                Leg leg = new Leg();
                                leg.setRaceId(raceId);
                                leg.setLegNumber(legNumber);
                                leg.setLegName(betTypeCode + "-" + legNumber);
                                raceCard.getLegs().add(leg);
                            }
                        }

                        if (raceCard.getLegs().size() == numberOfLegs) {
                            raceCards.add(raceCard);
                        }
                    }
                }
            }
        }
    }

    private void addMultiTrackRaceCards(ArrayOfMultipleTrackPoolSetup multipleTrackPoolSetups, Set<RaceCard> raceCards, LocalDate yesterday) {

        for (MultipleTrackPoolSetup mtps : multipleTrackPoolSetups.getMultipleTrackPoolSetup()) {

            LocalDate raceDayDate = Util.getLocalDateFromAtgDate(mtps.getRaceDayDate());

            if (raceDayDate.isAfter(yesterday)) {

                String betTypeCode = mtps.getBetType().getCode();

                if (betTypeCode.equals("V3") ||
                        betTypeCode.equals("V4") ||
                        betTypeCode.equals("V5") ||
                        betTypeCode.equals("V64") ||
                        betTypeCode.equals("V65") ||
                        betTypeCode.equals("V75") ||
                        betTypeCode.equals("GS75") ||
                        betTypeCode.equals("V86")) {

                    RaceCard raceCard = new RaceCard();
                    raceCard.setAtgTrackId(mtps.getTrackKey().getTrackId());
                    raceCard.setAtgTrackCode(mtps.getTrack().getCode());
                    raceCard.setBetType(betTypeCode);
                    raceCard.setRaceDayDate(raceDayDate);
                    raceCard.setTrackName(mtps.getMultipleTrackName());
                    raceCard.setHasBoost(mtps.isHasBoost());

                    //add legs

                    if (mtps.getLegInfo() != null) {

                        for (LegInfo legInfo : mtps.getLegInfo().getLegInfo()) {

                            if (legInfo.getHostTrack() != null && legInfo.getHostTrack().getTrack() != null && legInfo.getHostTrack().getTrackKey() != null) {

                                int atgTrackId = legInfo.getHostTrack().getTrackKey().getTrackId();
                                String atgTrackCode = legInfo.getHostTrack().getTrack().getCode();

                                Integer raceId = fetchRaceIdService.fetch(raceDayDate, atgTrackId, atgTrackCode, legInfo.getRaceNr());

                                if (raceId != null) {
                                    Leg leg = new Leg();
                                    leg.setRaceId(raceId);
                                    leg.setLegNumber(legInfo.getLegNr());
                                    leg.setLegName(betTypeCode + "-" + legInfo.getLegNr());
                                    raceCard.getLegs().add(leg);
                                }
                            }
                        }

                        if (raceCard.getLegs().size() == Util.getNumberOfLegs(betTypeCode)) {
                            raceCards.add(raceCard);
                        }
                    }
                }
            }
        }
    }
}
