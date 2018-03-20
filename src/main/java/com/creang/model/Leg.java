package com.creang.model;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Leg {

    private int id;
    private int raceId;
    private int raceCardId;
    private int legNumber;
    private String legName;
    private boolean hasResult;
    private Integer luckyHorse;
    private String reserveOrder;
    private int marksQuantity;
    private BigDecimal systemsLeft;
    private BigDecimal value;
    private Set<LegParticipant> legParticipants;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public int getRaceCardId() {
        return raceCardId;
    }

    public void setRaceCardId(int raceCardId) {
        this.raceCardId = raceCardId;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public void setLegNumber(int legNumber) {
        this.legNumber = legNumber;
    }

    public String getLegName() {
        return legName;
    }

    public void setLegName(String legName) {
        this.legName = legName;
    }

    public boolean isHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }

    public Integer getLuckyHorse() {
        return luckyHorse;
    }

    public void setLuckyHorse(Integer luckyHorse) {
        this.luckyHorse = luckyHorse;
    }

    public String getReserveOrder() {
        return reserveOrder;
    }

    public void setReserveOrder(String reserveOrder) {
        this.reserveOrder = reserveOrder;
    }

    public int getMarksQuantity() {
        return marksQuantity;
    }

    public void setMarksQuantity(int marksQuantity) {
        this.marksQuantity = marksQuantity;
    }

    public BigDecimal getSystemsLeft() {
        return systemsLeft;
    }

    public void setSystemsLeft(BigDecimal systemsLeft) {
        this.systemsLeft = systemsLeft;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Set<LegParticipant> getLegParticipants() {
        if (legParticipants == null) {
            legParticipants = new LinkedHashSet<>();
        }
        return legParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return raceId == leg.raceId &&
                raceCardId == leg.raceCardId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceId, raceCardId);
    }
}
