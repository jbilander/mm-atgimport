package com.creang.service;

import aisbean.*;
import com.creang.common.MyProperties;
import com.creang.common.Util;
import se.atg.webservice.info.PartnerInfoService;
import se.atg.webservice.info.PartnerInfoServicePort;

import javax.xml.ws.BindingProvider;
import java.time.LocalDate;

public class AtgPartnerInfoService {

    private final PartnerInfoServicePort service = new PartnerInfoService().getPartnerInfoServicePort();

    public AtgPartnerInfoService() {
        BindingProvider bindingProvider = (BindingProvider) service;
        MyProperties properties = MyProperties.getInstance();
        String username = properties.getProperties().getProperty(Util.AIS_USERNAME_KEY);
        String password = properties.getProperties().getProperty(Util.AIS_PASSWORD_KEY);
        bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
        bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    public VPResult fetchVPResultRace(LocalDate raceDayDate, int atgTrackId, int raceNumber) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchVPResultRace(atgDate, trackKey, raceNumber);
        } catch (Exception e) {
            throw e;
        }
    }

    public MarkingBetResult fetchMarkingBetResult(LocalDate raceDayDate, int atgTrackId, String betType) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchMarkingBetResult(atgDate, trackKey, betType);
        } catch (Exception e) {
            throw e;
        }
    }

    public MarkingBetReserveOrder fetchMarkingBetReserveOrder(LocalDate raceDayDate, int atgTrackId, String betType) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchMarkingBetReserveOrder(atgDate, trackKey, betType);
        } catch (Exception e) {
            throw e;
        }
    }

    public MarkingBetPoolInfo fetchMarkingBetPoolInfo(LocalDate raceDayDate, int atgTrackId, String betType) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchMarkingBetPoolInfo(atgDate, trackKey, betType);
        } catch (Exception e) {
            throw e;
        }
    }

    public TrackBetInfo fetchTrackBetInfo(LocalDate raceDayDate, int atgTrackId) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchTrackBetInfo(atgDate, trackKey);
        } catch (Exception e) {
            throw e;
        }
    }

    public VPPoolInfo fetchVPPoolInfoRace(LocalDate raceDayDate, int atgTrackId, int raceNumber) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchVPPoolInfoRace(atgDate, trackKey, raceNumber);
        } catch (Exception e) {
            throw e;
        }
    }

    public RaceDayCalendar fetchRaceDayCalendar() throws Exception {
        try {
            return service.fetchRaceDayCalendar();
        } catch (Exception e) {
            throw e;
        }
    }

    public RacingCard fetchRacingCardRace(LocalDate raceDayDate, int atgTrackId, int raceNumber) throws Exception {

        AtgDate atgDate = Util.getAtgDateFromLocalDate(raceDayDate);
        TrackKey trackKey = Util.getTrackKey(atgTrackId);

        try {
            return service.fetchRacingCardRace(atgDate, trackKey, raceNumber);
        } catch (Exception e) {
            throw e;
        }
    }
}
