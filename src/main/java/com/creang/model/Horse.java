package com.creang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Horse {

    private int id;
    private int keyId;
    private String name;
    private int age;
    private String gender;
    private int foreShoes;
    private int hindShoes;
    private String sire;
    private String dam;
    private String damSire;
    private String homeTrack;
    private String color;
    private String blinkersType;
    private Integer rating;
    private int startPoint;
    private List<Record> records;
    private List<YearStat> yearStats;
    private List<PastPerformance> pastPerformances;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getForeShoes() {
        return foreShoes;
    }

    public void setForeShoes(int foreShoes) {
        this.foreShoes = foreShoes;
    }

    public int getHindShoes() {
        return hindShoes;
    }

    public void setHindShoes(int hindShoes) {
        this.hindShoes = hindShoes;
    }

    public String getSire() {
        return sire;
    }

    public void setSire(String sire) {
        this.sire = sire;
    }

    public String getDam() {
        return dam;
    }

    public void setDam(String dam) {
        this.dam = dam;
    }

    public String getDamSire() {
        return damSire;
    }

    public void setDamSire(String damSire) {
        this.damSire = damSire;
    }

    public String getHomeTrack() {
        return homeTrack;
    }

    public void setHomeTrack(String homeTrack) {
        this.homeTrack = homeTrack;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBlinkersType() {
        return blinkersType;
    }

    public void setBlinkersType(String blinkersType) {
        this.blinkersType = blinkersType;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }

    public List<Record> getRecords() {
        if (records == null) {
            records = new ArrayList<>();
        }
        return records;
    }

    public List<YearStat> getYearStats() {
        if (yearStats == null) {
            yearStats = new ArrayList<>();
        }
        return yearStats;
    }

    public List<PastPerformance> getPastPerformances() {
        if (pastPerformances == null) {
            pastPerformances = new ArrayList<>();
        }
        return pastPerformances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Horse horse = (Horse) o;
        return id == horse.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
