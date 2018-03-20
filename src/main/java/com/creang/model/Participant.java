package com.creang.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Participant {

    private int id;
    private int raceId;
    private int startNumber;
    private int distance;
    private int startPosition;
    private boolean scratched;
    private String scratchedReason;
    private boolean driverChanged;
    private String driverColor;
    private BigDecimal winnerOdds;
    private BigDecimal investmentWinner;
    private BigDecimal cardWeight;
    private boolean cardWeightChanged;
    private BigDecimal conditionWeight;
    private BigDecimal parWeight1;
    private BigDecimal parWeight2;
    private BigDecimal plusNumberWeight;
    private Horse horse;
    private Driver driver;
    private Trainer trainer;

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

    public int getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
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

    public boolean isDriverChanged() {
        return driverChanged;
    }

    public void setDriverChanged(boolean driverChanged) {
        this.driverChanged = driverChanged;
    }

    public String getDriverColor() {
        return driverColor;
    }

    public void setDriverColor(String driverColor) {
        this.driverColor = driverColor;
    }

    public BigDecimal getWinnerOdds() {
        return winnerOdds;
    }

    public void setWinnerOdds(BigDecimal winnerOdds) {
        this.winnerOdds = winnerOdds;
    }

    public BigDecimal getInvestmentWinner() {
        return investmentWinner;
    }

    public void setInvestmentWinner(BigDecimal investmentWinner) {
        this.investmentWinner = investmentWinner;
    }

    public BigDecimal getCardWeight() {
        return cardWeight;
    }

    public void setCardWeight(BigDecimal cardWeight) {
        this.cardWeight = cardWeight;
    }

    public boolean isCardWeightChanged() {
        return cardWeightChanged;
    }

    public void setCardWeightChanged(boolean cardWeightChanged) {
        this.cardWeightChanged = cardWeightChanged;
    }

    public BigDecimal getConditionWeight() {
        return conditionWeight;
    }

    public void setConditionWeight(BigDecimal conditionWeight) {
        this.conditionWeight = conditionWeight;
    }

    public BigDecimal getParWeight1() {
        return parWeight1;
    }

    public void setParWeight1(BigDecimal parWeight1) {
        this.parWeight1 = parWeight1;
    }

    public BigDecimal getParWeight2() {
        return parWeight2;
    }

    public void setParWeight2(BigDecimal parWeight2) {
        this.parWeight2 = parWeight2;
    }

    public BigDecimal getPlusNumberWeight() {
        return plusNumberWeight;
    }

    public void setPlusNumberWeight(BigDecimal plusNumberWeight) {
        this.plusNumberWeight = plusNumberWeight;
    }

    public Horse getHorse() {
        return horse;
    }

    public void setHorse(Horse horse) {
        this.horse = horse;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return raceId == that.raceId &&
                startNumber == that.startNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceId, startNumber);
    }
}
