package com.creang.model;

import java.math.BigDecimal;
import java.util.Objects;

public class YearStat {

    private int id;
    private int horseId;
    private int yearStatType;
    private BigDecimal earnings;
    private int first;
    private int second;
    private int third;
    private int numberOfStarts;
    private BigDecimal showPercentage;
    private BigDecimal winPercentage;
    private int year;

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

    public int getYearStatType() {
        return yearStatType;
    }

    public void setYearStatType(int yearStatType) {
        this.yearStatType = yearStatType;
    }

    public BigDecimal getEarnings() {
        return earnings;
    }

    public void setEarnings(BigDecimal earnings) {
        this.earnings = earnings;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public int getNumberOfStarts() {
        return numberOfStarts;
    }

    public void setNumberOfStarts(int numberOfStarts) {
        this.numberOfStarts = numberOfStarts;
    }

    public BigDecimal getShowPercentage() {
        return showPercentage;
    }

    public void setShowPercentage(BigDecimal showPercentage) {
        this.showPercentage = showPercentage;
    }

    public BigDecimal getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(BigDecimal winPercentage) {
        this.winPercentage = winPercentage;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearStat yearStat = (YearStat) o;
        return id == yearStat.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
