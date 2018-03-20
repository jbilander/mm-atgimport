package com.creang.model;

import java.math.BigDecimal;
import java.util.Objects;

public class PayOut {

    private int raceCardId;
    private int numberOfCorrects;
    private BigDecimal numberOfSystems;
    private BigDecimal payOutAmount;
    private BigDecimal totalAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayOut payOut = (PayOut) o;
        return raceCardId == payOut.raceCardId &&
                numberOfCorrects == payOut.numberOfCorrects;
    }

    @Override
    public int hashCode() {
        return Objects.hash(raceCardId, numberOfCorrects);
    }

    public int getRaceCardId() {
        return raceCardId;
    }

    public void setRaceCardId(int raceCardId) {
        this.raceCardId = raceCardId;
    }

    public int getNumberOfCorrects() {
        return numberOfCorrects;
    }

    public void setNumberOfCorrects(int numberOfCorrects) {
        this.numberOfCorrects = numberOfCorrects;
    }

    public BigDecimal getNumberOfSystems() {
        return numberOfSystems;
    }

    public void setNumberOfSystems(BigDecimal numberOfSystems) {
        this.numberOfSystems = numberOfSystems;
    }

    public BigDecimal getPayOutAmount() {
        return payOutAmount;
    }

    public void setPayOutAmount(BigDecimal payOutAmount) {
        this.payOutAmount = payOutAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
