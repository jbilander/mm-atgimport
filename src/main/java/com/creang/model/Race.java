package com.creang.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Race {

    private int id;
    private LocalDate raceDayDate;
    private LocalTime postTime;
    private int raceNumber;
    private int atgTrackId;
    private String atgTrackCode;
    private String trackName;
    private LocalDateTime updated;
    private String raceName;
    private String longDesc;
    private String shortDesc;
    private int distance;
    private String startMethod;
    private String trackSurface;
    private boolean hasResult;
    private boolean hasParticipants;
    private String trackState;
    private boolean monte;
    private boolean gallop;
    private BigDecimal winTurnOver;
    private Set<Participant> participants;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalTime postTime) {
        this.postTime = postTime;
    }

    public LocalDate getRaceDayDate() {
        return raceDayDate;
    }

    public void setRaceDayDate(LocalDate raceDayDate) {
        this.raceDayDate = raceDayDate;
    }

    public int getAtgTrackId() {
        return atgTrackId;
    }

    public void setAtgTrackId(int atgTrackId) {
        this.atgTrackId = atgTrackId;
    }

    public String getAtgTrackCode() {
        return atgTrackCode;
    }

    public void setAtgTrackCode(String atgTrackCode) {
        this.atgTrackCode = atgTrackCode;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public int getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(int raceNumber) {
        this.raceNumber = raceNumber;
    }

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStartMethod() {
        return startMethod;
    }

    public void setStartMethod(String startMethod) {
        this.startMethod = startMethod;
    }

    public String getTrackSurface() {
        return trackSurface;
    }

    public void setTrackSurface(String trackSurface) {
        this.trackSurface = trackSurface;
    }

    public String getTrackState() {
        return trackState;
    }

    public void setTrackState(String trackState) {
        this.trackState = trackState;
    }

    public boolean isHasParticipants() {
        return hasParticipants;
    }

    public void setHasParticipants(boolean hasParticipants) {
        this.hasParticipants = hasParticipants;
    }

    public boolean isMonte() {
        return monte;
    }

    public void setMonte(boolean monte) {
        this.monte = monte;
    }

    public boolean isGallop() {
        return gallop;
    }

    public void setGallop(boolean gallop) {
        this.gallop = gallop;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public boolean isHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }

    public BigDecimal getWinTurnOver() {
        return winTurnOver;
    }

    public void setWinTurnOver(BigDecimal winTurnOver) {
        this.winTurnOver = winTurnOver;
    }

    public Set<Participant> getParticipants() {
        if (participants == null) {
            participants = new LinkedHashSet<>();
        }
        return participants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Race race = (Race) o;
        return atgTrackId == race.atgTrackId &&
                raceNumber == race.raceNumber &&
                Objects.equals(raceDayDate, race.raceDayDate) &&
                Objects.equals(atgTrackCode, race.atgTrackCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceDayDate, atgTrackId, atgTrackCode, raceNumber);
    }
}
