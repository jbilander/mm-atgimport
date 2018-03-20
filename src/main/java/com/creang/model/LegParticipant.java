package com.creang.model;

import java.math.BigDecimal;
import java.util.Objects;

public class LegParticipant {

    private int legId;
    private int participantId;
    private int startNumber;
    private BigDecimal percentage;
    private int quantity;
    private boolean legWinner;

    public int getLegId() {
        return legId;
    }

    public void setLegId(int legId) {
        this.legId = legId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(int startNumber) {
        this.startNumber = startNumber;
    }

    public boolean isLegWinner() {
        return legWinner;
    }

    public void setLegWinner(boolean legWinner) {
        this.legWinner = legWinner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegParticipant that = (LegParticipant) o;
        return legId == that.legId &&
                participantId == that.participantId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(legId, participantId);
    }
}
