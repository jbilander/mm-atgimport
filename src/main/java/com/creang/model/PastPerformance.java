package com.creang.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PastPerformance {

    private int id;
    private int horseId;
    private Integer distance;
    private boolean monte;
    private boolean gallopRace;
    private Integer startPosition;
    private Boolean foreShoes;
    private Boolean hindShoes;
    private String trackState;
    private String driverShortName;
    private BigDecimal earning;
    private BigDecimal firstPrize;
    private String formattedTime;
    private String odds;
    private String formattedResult;
    private boolean scratched;
    private String scratchedReason;
    private LocalDate raceDate;
    private Integer raceNumber;
    private String atgTrackCode;
    private Integer startNumber;
    private String raceTime;
    private boolean blinkers;
    private String blinkersType;
    private int rating;
    private String trackSurface;
    private BigDecimal weight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHorseId() {
        return horseId;
    }

    public void setHorseId(int horseId) {
        this.horseId = horseId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public boolean isMonte() {
        return monte;
    }

    public void setMonte(boolean monte) {
        this.monte = monte;
    }

    public boolean isGallopRace() {
        return gallopRace;
    }

    public void setGallopRace(boolean gallopRace) {
        this.gallopRace = gallopRace;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Boolean getForeShoes() {
        return foreShoes;
    }

    public void setForeShoes(Boolean foreShoes) {
        this.foreShoes = foreShoes;
    }

    public Boolean getHindShoes() {
        return hindShoes;
    }

    public void setHindShoes(Boolean hindShoes) {
        this.hindShoes = hindShoes;
    }

    public String getTrackState() {
        return trackState;
    }

    public void setTrackState(String trackState) {
        this.trackState = trackState;
    }

    public String getDriverShortName() {
        return driverShortName;
    }

    public void setDriverShortName(String driverShortName) {
        this.driverShortName = driverShortName;
    }

    public BigDecimal getEarning() {
        return earning;
    }

    public void setEarning(BigDecimal earning) {
        this.earning = earning;
    }

    public BigDecimal getFirstPrize() {
        return firstPrize;
    }

    public void setFirstPrize(BigDecimal firstPrize) {
        this.firstPrize = firstPrize;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public String getFormattedResult() {
        return formattedResult;
    }

    public void setFormattedResult(String formattedResult) {
        this.formattedResult = formattedResult;
    }

    public boolean isScratched() {
        return scratched;
    }

    public void setScratched(boolean scratched) {
        this.scratched = scratched;
    }

    public String getScratchedReason() {
        return scratchedReason;
    }

    public void setScratchedReason(String scratchedReason) {
        this.scratchedReason = scratchedReason;
    }

    public LocalDate getRaceDate() {
        return raceDate;
    }

    public void setRaceDate(LocalDate raceDate) {
        this.raceDate = raceDate;
    }

    public Integer getRaceNumber() {
        return raceNumber;
    }

    public void setRaceNumber(Integer raceNumber) {
        this.raceNumber = raceNumber;
    }

    public String getAtgTrackCode() {
        return atgTrackCode;
    }

    public void setAtgTrackCode(String atgTrackCode) {
        this.atgTrackCode = atgTrackCode;
    }

    public Integer getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(Integer startNumber) {
        this.startNumber = startNumber;
    }

    public String getRaceTime() {
        return raceTime;
    }

    public void setRaceTime(String raceTime) {
        this.raceTime = raceTime;
    }

    public boolean isBlinkers() {
        return blinkers;
    }

    public void setBlinkers(boolean blinkers) {
        this.blinkers = blinkers;
    }

    public String getBlinkersType() {
        return blinkersType;
    }

    public void setBlinkersType(String blinkersType) {
        this.blinkersType = blinkersType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTrackSurface() {
        return trackSurface;
    }

    public void setTrackSurface(String trackSurface) {
        this.trackSurface = trackSurface;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
