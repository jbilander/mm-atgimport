package com.creang.task;

import aisbean.*;
import com.creang.common.MyProperties;
import com.creang.common.ShoeInfo;
import com.creang.common.Util;
import com.creang.model.Horse;
import com.creang.model.*;
import com.creang.model.PayOut;
import com.creang.model.Race;
import com.creang.service.AtgPartnerInfoService;
import com.creang.service.db.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.creang.Main.loggerUtil;

public class UpdateRaceAndRaceCardTask {

    private final Logger logger = loggerUtil.getLogger();
    private final AtgPartnerInfoService atgPartnerInfoService = new AtgPartnerInfoService();
    private final FetchActiveRacesService fetchActiveRacesService = new FetchActiveRacesService();
    private final FetchParticipantService fetchParticipantService = new FetchParticipantService();
    private final FetchActiveRaceCardsService fetchActiveRaceCardsService = new FetchActiveRaceCardsService();
    private final UpdateRaceService updateRaceService = new UpdateRaceService();
    private final UpdateRaceCardService updateRaceCardService = new UpdateRaceCardService();

    public void run(LocalDate fromDate, LocalDate toDate) {

        MyProperties properties = MyProperties.getInstance();
        LocalDate now = LocalDate.now(ZoneId.of(properties.getProperties().getProperty(Util.TIME_ZONE_KEY)));

        logger.info("UpdateRaceAndRaceCardTask: Start");
        processRaces(fromDate, toDate, now);
        processRaceCards(fromDate, toDate, now);
        logger.info("UpdateRaceAndRaceCardTask: Done");
    }

    private void processRaces(LocalDate fromDate, LocalDate toDate, LocalDate now) {

        Map<Integer, List<Race>> raceMap = fetchActiveRacesService.fetch(fromDate, toDate);
        List<Race> racesToUpdate = new ArrayList<>();

        for (List<Race> races : raceMap.values()) {

            Race firstRace = races.get(0);
            TrackBetInfo trackBetInfo = null;

            if (firstRace.getRaceDayDate().isEqual(now)) {
                try {
                    trackBetInfo = atgPartnerInfoService.fetchTrackBetInfo(firstRace.getRaceDayDate(), firstRace.getAtgTrackId());
                } catch (Exception ex) {
                    logger.severe("Exception fetchTrackBetInfo: " + ex.getMessage());
                }
            }

            for (Race race : races) {

                try {

                    VPPoolInfo vpPoolInfo = atgPartnerInfoService.fetchVPPoolInfoRace(race.getRaceDayDate(), race.getAtgTrackId(), race.getRaceNumber());
                    List<Participant> participants = fetchParticipantService.fetch(race.getId());
                    race.getParticipants().addAll(participants);

                    if (vpPoolInfo.getVpOdds() != null && vpPoolInfo.getVpOdds().getVPOdds().size() > 0 && participants.size() > 0) {
                        processVPPoolInfo(race, vpPoolInfo, participants);
                        racesToUpdate.add(race);
                    }

                    if (trackBetInfo != null) {
                        processTrackBetInfo(race, trackBetInfo, participants);
                    }

                    if (race.getRaceDayDate().isEqual(now)) {

                        VPResult vpResult = atgPartnerInfoService.fetchVPResultRace(race.getRaceDayDate(), race.getAtgTrackId(), race.getRaceNumber());

                        if (vpResult != null && vpResult.getToteStarts() != null && vpResult.getToteStarts().getToteStart().size() > 0) {
                            race.setHasResult(true);
                        }
                    }

                } catch (Exception ex) {
                    logger.severe("Exception processRaces: " + ex.getMessage());
                }
            }
        }

        if (racesToUpdate.size() > 0) {
            updateRaceService.update(racesToUpdate);
        }
    }

    private void processRaceCards(LocalDate fromDate, LocalDate toDate, LocalDate now) {

        List<RaceCard> raceCards = fetchActiveRaceCardsService.fetch(fromDate, toDate);
        List<RaceCard> raceCardsToUpdate = new ArrayList<>();

        for (RaceCard raceCard : raceCards) {

            try {
                MarkingBetPoolInfo markingBetPoolInfo = atgPartnerInfoService.fetchMarkingBetPoolInfo(raceCard.getRaceDayDate(), raceCard.getAtgTrackId(), raceCard.getBetType());

                if (markingBetPoolInfo != null) {
                    processMarkingBetPoolInfo(raceCard, markingBetPoolInfo);
                    raceCardsToUpdate.add(raceCard);
                }

            } catch (Exception ex) {
                logger.severe("Exception fetchMarkingBetPoolInfo: " + ex.getMessage());
            }

            if (raceCard.getRaceDayDate().isEqual(now)) {

                try {
                    MarkingBetResult markingBetResult = atgPartnerInfoService.fetchMarkingBetResult(raceCard.getRaceDayDate(), raceCard.getAtgTrackId(), raceCard.getBetType());

                    if (markingBetResult != null) {
                        processMarkingBetResult(raceCard, markingBetResult);

                        MarkingBetReserveOrder markingBetReserveOrder = atgPartnerInfoService.fetchMarkingBetReserveOrder(raceCard.getRaceDayDate(), raceCard.getAtgTrackId(), raceCard.getBetType());

                        if (markingBetReserveOrder != null) {
                            processMarkingBetReserveOrder(raceCard, markingBetReserveOrder);
                        }
                    }
                } catch (Exception ex) {
                    logger.severe("Exception processRaceCards: " + ex.getMessage());
                }
            }
        }

        if (raceCardsToUpdate.size() > 0) {
            updateRaceCardService.update(raceCardsToUpdate);
        }
    }

    private void processMarkingBetReserveOrder(RaceCard raceCard, MarkingBetReserveOrder markingBetReserveOrder) {

        if (markingBetReserveOrder.getLegReserveOrders() != null && markingBetReserveOrder.getLegReserveOrders().getLegReserveOrder().size() > 0) {

            for (LegReserveOrder lro : markingBetReserveOrder.getLegReserveOrders().getLegReserveOrder()) {

                Leg leg = raceCard.getLegs().stream().filter(l -> l.getLegNumber() == lro.getLegNr()).findFirst().orElse(null);

                if (leg != null) {

                    if (lro.getReserveOrder() != null) {
                        leg.setReserveOrder(lro.getReserveOrder());
                    }

                    if (lro.getLuckyHorse() != null) {
                        leg.setLuckyHorse(lro.getLuckyHorse());
                    }
                }
            }
        }
    }

    private void processMarkingBetResult(RaceCard raceCard, MarkingBetResult markingBetResult) {

        raceCard.setMadeBetsQuantity(markingBetResult.getNrOfSystems());

        if (markingBetResult.getTurnover() != null) {
            raceCard.setTurnOver(markingBetResult.getTurnover().getSum());
        }

        if (markingBetResult.getBoostResult() != null) {
            raceCard.setBoostNumber(Integer.parseInt(markingBetResult.getBoostResult().getBoostNumber()));
        }

        if (markingBetResult.getTimestamp() != null && markingBetResult.getTimestamp().getDate() != null && markingBetResult.getTimestamp().getTime() != null) {
            raceCard.setUpdated(Util.getLocalDateTimeFromAtgDateTime(markingBetResult.getTimestamp()));
        }

        if (markingBetResult.getLegResults() != null && markingBetResult.getLegResults().getLegResult().size() > 0) {
            for (LegResult legResult : markingBetResult.getLegResults().getLegResult()) {
                Leg leg = raceCard.getLegs().stream().filter(l -> l.getLegNumber() == legResult.getLegNr()).findFirst().orElse(null);

                if (leg != null) {

                    if (legResult.getLuckyHorse() != null) {
                        leg.setLuckyHorse(legResult.getLuckyHorse());
                    }

                    if (legResult.getValue() != null) {
                        leg.setValue(BigDecimal.valueOf(legResult.getValue()));
                    }

                    if (legResult.getSystemsLeft() != null) {
                        leg.setSystemsLeft(legResult.getSystemsLeft());
                    }

                    if (legResult.getWinners() != null && legResult.getWinners().getInt().size() > 0) {

                        leg.getLegParticipants().forEach(lp -> lp.setLegWinner(false));

                        for (Integer winnerStartNumber : legResult.getWinners().getInt()) {

                            LegParticipant legParticipant = leg.getLegParticipants().stream().filter(lp -> lp.getStartNumber() == winnerStartNumber).findFirst().orElse(null);

                            if (legParticipant != null) {
                                legParticipant.setLegWinner(true);
                                leg.setHasResult(true);
                            }
                        }
                    }
                }
            }
        }

        if (markingBetResult.getPayOuts() != null && markingBetResult.getPayOuts().getPayOut().size() > 0) {
            raceCard.getPayOuts().clear();
            processPayOuts(raceCard, markingBetResult.getPayOuts().getPayOut());
        }

        if (raceCard.getLegs().stream().anyMatch(Leg::isHasResult)) {
            raceCard.setHasResult(true);
        }

        if (markingBetResult.isResultComplete() && raceCard.getLegs().stream().allMatch(Leg::isHasResult)) {
            raceCard.setHasCompleteResult(true);
        }
    }

    private void processMarkingBetPoolInfo(RaceCard raceCard, MarkingBetPoolInfo markingBetPoolInfo) {

        raceCard.setMadeBetsQuantity(markingBetPoolInfo.getMadeBetsQty());

        if (markingBetPoolInfo.getTurnover() != null) {
            raceCard.setTurnOver(markingBetPoolInfo.getTurnover().getSum());
        }

        if (markingBetPoolInfo.getJackpot() != null) {
            raceCard.setJackpotSum(markingBetPoolInfo.getJackpot().getSum());
        }

        if (markingBetPoolInfo.getTimestamp() != null && markingBetPoolInfo.getTimestamp().getDate() != null && markingBetPoolInfo.getTimestamp().getTime() != null) {
            raceCard.setUpdated(Util.getLocalDateTimeFromAtgDateTime(markingBetPoolInfo.getTimestamp()));
        }

        //region MarkingBetLegs
        if (markingBetPoolInfo.getMarkingBetLegs() != null && markingBetPoolInfo.getMarkingBetLegs().getMarkingBetLeg().size() > 0) {

            for (MarkingBetLeg markingBetLeg : markingBetPoolInfo.getMarkingBetLegs().getMarkingBetLeg()) {

                Leg leg = raceCard.getLegs().stream().filter(l -> l.getLegNumber() == markingBetLeg.getLegNr()).findFirst().orElse(null);

                if (leg != null && markingBetLeg.getHorseMarks() != null && markingBetLeg.getHorseMarks().getMarkInfo().size() > 0) {

                    if (raceCard.getBetType().equals("V3")) {
                        leg.setMarksQuantity(markingBetLeg.getMarksInLegQty());
                    } else {
                        leg.setMarksQuantity(markingBetLeg.getHorseMarks().getMarkInfo().stream().mapToInt(MarkInfo::getStakeDistribution).sum());
                    }

                    if (leg.getMarksQuantity() > 0) {

                        for (MarkInfo markInfo : markingBetLeg.getHorseMarks().getMarkInfo()) {

                            LegParticipant legParticipant = leg.getLegParticipants().stream().filter(lp -> lp.getStartNumber() == markInfo.getStartingNr()).findFirst().orElse(null);

                            if (legParticipant != null) {

                                if (raceCard.getBetType().equals("V3")) {
                                    legParticipant.setQuantity(markInfo.getQuantity());
                                } else {
                                    legParticipant.setQuantity(markInfo.getStakeDistribution());
                                }

                                BigDecimal value = BigDecimal.valueOf(legParticipant.getQuantity() * 100L);
                                legParticipant.setPercentage(value.divide(BigDecimal.valueOf(leg.getMarksQuantity()), 9, RoundingMode.HALF_UP));
                            }
                        }
                    }
                }
            }

            if (markingBetPoolInfo.getPayOuts() != null && markingBetPoolInfo.getPayOuts().getPayOut().size() > 0) {
                raceCard.getPayOuts().clear();
                processPayOuts(raceCard, markingBetPoolInfo.getPayOuts().getPayOut());
            }
        }
        //endregion MarkingBetLegs
    }

    private void processPayOuts(RaceCard raceCard, List<aisbean.PayOut> payOuts) {
        for (aisbean.PayOut atgPayOut : payOuts) {
            PayOut payOut = new PayOut();
            payOut.setRaceCardId(raceCard.getId());
            payOut.setNumberOfCorrects(atgPayOut.getNrOfCorrects());
            payOut.setNumberOfSystems(atgPayOut.getNrOfSystems());
            payOut.setPayOutAmount(atgPayOut.getPayOutAmount());
            payOut.setTotalAmount(atgPayOut.getTotalAmount());
            raceCard.getPayOuts().add(payOut);
        }
    }

    private void processTrackBetInfo(Race race, TrackBetInfo trackBetInfo, List<Participant> participants) {


        if (trackBetInfo.getDriverChanged() != null && trackBetInfo.getDriverChanged().getDriverChanged().size() > 0) {
            processDriverChanges(race, trackBetInfo.getDriverChanged().getDriverChanged(), participants);
        }

        if (trackBetInfo.getWeightChanged() != null && trackBetInfo.getWeightChanged().getWeightChanged().size() > 0) {
            processWeightChanges(race, trackBetInfo.getWeightChanged().getWeightChanged(), participants);
        }

        if (trackBetInfo.getShoeInfoRace() != null && trackBetInfo.getShoeInfoRace().getShoeInfoRace().size() > 0) {
            processShoeInfo(race, trackBetInfo.getShoeInfoRace().getShoeInfoRace(), participants);
        }

        if (trackBetInfo.getTrackInfo() != null && trackBetInfo.getTrackInfo().getTrackCondition() != null
                && trackBetInfo.getTrackInfo().getTrackCondition().getTrackCondition().size() > 0) {

            TrackCondition tc = trackBetInfo.getTrackInfo().getTrackCondition().getTrackCondition().get(0);

            if (tc.getCondition() != null) {
                race.setTrackState(tc.getCondition().getCode());
            }
            if (tc.getSurface() != null) {
                race.setTrackSurface(tc.getSurface().getCode());
            }
        }
    }

    private void processShoeInfo(Race race, List<ShoeInfoRace> shoeInfoRaces, List<Participant> participants) {

        ShoeInfoRace shoeInfoRace = shoeInfoRaces.stream().filter(sir -> sir.getRaceNr() == race.getRaceNumber()).findFirst().orElse(null);

        if (shoeInfoRace != null && shoeInfoRace.getShoeInfoStart() != null) {

            for (ShoeInfoStart shoeInfo : shoeInfoRace.getShoeInfoStart().getShoeInfoStart()) {

                Participant participant = participants.stream().filter(p -> p.getStartNumber() == shoeInfo.getStartNr()).findFirst().orElse(null);

                if (participant != null) {

                    Horse horse = participant.getHorse();

                    //region foreshoes
                    if (shoeInfo.getShoeInfoPrevious() != null && shoeInfo.getShoeInfoPrevious().isForeshoes() != null
                            && shoeInfo.getShoeInfoCurrent() != null && shoeInfo.getShoeInfoCurrent().isForeshoes() != null) {

                        if (shoeInfo.getShoeInfoPrevious().isForeshoes()) { //Went with foreshoes on in previous start

                            int foreshoes = shoeInfo.getShoeInfoCurrent().isForeshoes() ? ShoeInfo.on : ShoeInfo.offRed;
                            horse.setForeShoes(foreshoes);

                        } else { //Went without foreshoes in previous start

                            int foreshoes = shoeInfo.getShoeInfoCurrent().isForeshoes() ? ShoeInfo.onRed : ShoeInfo.off;
                            horse.setForeShoes(foreshoes);
                        }
                    }

                    if ((shoeInfo.getShoeInfoPrevious() == null || shoeInfo.getShoeInfoPrevious().isForeshoes() == null)
                            && shoeInfo.getShoeInfoCurrent() != null && shoeInfo.getShoeInfoCurrent().isForeshoes() != null) {

                        int foreshoes = shoeInfo.getShoeInfoCurrent().isForeshoes() ? ShoeInfo.on : ShoeInfo.off;
                        horse.setForeShoes(foreshoes);
                    }
                    //endregion foreshoes

                    //region hindshoes
                    if (shoeInfo.getShoeInfoPrevious() != null && shoeInfo.getShoeInfoPrevious().isHindshoes() != null
                            && shoeInfo.getShoeInfoCurrent() != null && shoeInfo.getShoeInfoCurrent().isHindshoes() != null) {

                        if (shoeInfo.getShoeInfoPrevious().isHindshoes()) { //Went with hindshoes on in previous start

                            int hindshoes = shoeInfo.getShoeInfoCurrent().isHindshoes() ? ShoeInfo.on : ShoeInfo.offRed;
                            horse.setHindShoes(hindshoes);

                        } else { //Went without hindshoes in previous start

                            int hindshoes = shoeInfo.getShoeInfoCurrent().isHindshoes() ? ShoeInfo.onRed : ShoeInfo.off;
                            horse.setHindShoes(hindshoes);
                        }
                    }

                    if ((shoeInfo.getShoeInfoPrevious() == null || shoeInfo.getShoeInfoPrevious().isHindshoes() == null)
                            && shoeInfo.getShoeInfoCurrent() != null && shoeInfo.getShoeInfoCurrent().isHindshoes() != null) {

                        int hindshoes = shoeInfo.getShoeInfoCurrent().isHindshoes() ? ShoeInfo.on : ShoeInfo.off;
                        horse.setHindShoes(hindshoes);
                    }
                    //endregion hindshoes
                }
            }
        }
    }

    private void processWeightChanges(Race race, List<WeightChanged> weightChanges, List<Participant> participants) {
        List<WeightChanged> weightChangedList = weightChanges.stream().filter(wc -> wc.getRaceNr() == race.getRaceNumber()).collect(Collectors.toList());

        if (weightChangedList.size() > 0) {
            Set<Integer> startNumbers = new HashSet<>();
            weightChangedList.forEach(wc -> startNumbers.add(wc.getStartNr()));

            for (Integer startNumber : startNumbers) {
                WeightChanged weightChanged = weightChangedList.stream().filter(wc -> wc.getStartNr() == startNumber).max(Comparator.comparing(a -> Util.getLocalDateTimeFromAtgDateTime(a.getTimestamp()))).orElse(null);

                if (weightChanged != null) {
                    Participant participant = participants.stream().filter(p -> p.getStartNumber() == startNumber).findFirst().orElse(null);

                    if (participant != null) {
                        participant.setCardWeight(BigDecimal.valueOf(weightChanged.getNewWeight()));
                        participant.setCardWeightChanged(true);
                    }
                }
            }
        }
    }

    private void processDriverChanges(Race race, List<DriverChanged> driverChanges, List<Participant> participants) {

        List<DriverChanged> driverChangedList = driverChanges.stream().filter(dc -> dc.getRaceNr() == race.getRaceNumber()).collect(Collectors.toList());

        if (driverChangedList.size() > 0) {
            Set<Integer> startNumbers = new HashSet<>();
            driverChangedList.forEach(dc -> startNumbers.add(dc.getStartNr()));

            for (Integer startNumber : startNumbers) {
                DriverChanged driverChanged = driverChangedList.stream().filter(dc -> dc.getStartNr() == startNumber).max(Comparator.comparing(a -> Util.getLocalDateTimeFromAtgDateTime(a.getTimestamp()))).orElse(null);

                if (driverChanged != null && driverChanged.getNewDriver() != null) {

                    Participant participant = participants.stream().filter(p -> p.getStartNumber() == startNumber).findFirst().orElse(null);

                    if (participant != null) {
                        LicenseOwner newDriver = driverChanged.getNewDriver();
                        participant.getDriver().setKeyId(newDriver.getId());
                        participant.getDriver().setFirstName(newDriver.getName());
                        participant.getDriver().setShortName(newDriver.getShortName());
                        participant.getDriver().setLastName(newDriver.getSurName());
                        participant.getDriver().setAmateur(newDriver.isAmateur());
                        participant.getDriver().setApprenticeAmateur(newDriver.isApprenticeAmateur());
                        participant.getDriver().setApprenticePro(newDriver.isApprenticePro());
                        participant.setDriverChanged(true);
                    }
                }
            }
        }
    }

    private void processVPPoolInfo(Race race, VPPoolInfo vpPoolInfo, List<Participant> participants) {

        if (vpPoolInfo.getTurnoverVinnare() != null && vpPoolInfo.getTurnoverVinnare().getSum() != null) {
            race.setWinTurnOver(vpPoolInfo.getTurnoverVinnare().getSum());
        }

        if (vpPoolInfo.getTimestamp() != null && vpPoolInfo.getTimestamp().getDate() != null && vpPoolInfo.getTimestamp().getTime() != null) {
            race.setUpdated(Util.getLocalDateTimeFromAtgDateTime(vpPoolInfo.getTimestamp()));
        }

        for (VPOdds vpOdds : vpPoolInfo.getVpOdds().getVPOdds()) {

            Participant participant = participants.stream().filter(p -> p.getStartNumber() == vpOdds.getStartNr()).findFirst().orElse(null);

            if (participant != null) {
                participant.setScratched(vpOdds.isScratched());
                if (vpOdds.getVinnarOdds() != null) {
                    participant.setWinnerOdds(BigDecimal.valueOf(vpOdds.getVinnarOdds().getOdds()));
                }
                if (vpOdds.getInvestmentVinnare() != null) {
                    participant.setInvestmentWinner(vpOdds.getInvestmentVinnare().getSum());
                }
            }
        }
    }
}
