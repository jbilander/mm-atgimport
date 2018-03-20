package com.creang.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class RaceCard {

    private int id;
    private String betType;
    private int atgTrackId;
    private String atgTrackCode;
    private LocalDate raceDayDate;
    private boolean activated;
    private BigDecimal jackpotSum;
    private BigDecimal turnOver;
    private String trackName;
    private LocalDateTime updated;
    private int madeBetsQuantity;
    private Integer boostNumber;
    private boolean hasBoost;
    private boolean hasResult;
    private boolean hasCompleteResult;
    private Set<Leg> legs;
    private Set<PayOut> payOuts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBetType() {
        return betType;
    }

    public void setBetType(String betType) {
        this.betType = betType;
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

    public boolean isHasBoost() {
        return hasBoost;
    }

    public void setHasBoost(boolean hasBoost) {
        this.hasBoost = hasBoost;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public BigDecimal getJackpotSum() {
        return jackpotSum;
    }

    public void setJackpotSum(BigDecimal jackpotSum) {
        this.jackpotSum = jackpotSum;
    }

    public BigDecimal getTurnOver() {
        return turnOver;
    }

    public void setTurnOver(BigDecimal turnOver) {
        this.turnOver = turnOver;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public int getMadeBetsQuantity() {
        return madeBetsQuantity;
    }

    public void setMadeBetsQuantity(int madeBetsQuantity) {
        this.madeBetsQuantity = madeBetsQuantity;
    }

    public Integer getBoostNumber() {
        return boostNumber;
    }

    public void setBoostNumber(Integer boostNumber) {
        this.boostNumber = boostNumber;
    }

    public boolean isHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }

    public boolean isHasCompleteResult() {
        return hasCompleteResult;
    }

    public void setHasCompleteResult(boolean hasCompleteResult) {
        this.hasCompleteResult = hasCompleteResult;
    }

    public Set<Leg> getLegs() {
        if (legs == null) {
            legs = new LinkedHashSet<>();
        }
        return legs;
    }

    public Set<PayOut> getPayOuts() {

        if (payOuts == null) {
            payOuts = new LinkedHashSet<>();
        }

        return payOuts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaceCard raceCard = (RaceCard) o;
        return atgTrackId == raceCard.atgTrackId &&
                Objects.equals(betType, raceCard.betType) &&
                Objects.equals(raceDayDate, raceCard.raceDayDate) &&
                Objects.equals(atgTrackCode, raceCard.atgTrackCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(betType, raceDayDate, atgTrackId, atgTrackCode);
    }
}
