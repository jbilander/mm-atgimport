package com.creang.model;

import java.util.Objects;

public class Driver {

    private int id;
    private int keyId;
    private String firstName;
    private String lastName;
    private String shortName;
    private boolean amateur;
    private boolean apprenticeAmateur;
    private boolean apprenticePro;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean isAmateur() {
        return amateur;
    }

    public void setAmateur(boolean amateur) {
        this.amateur = amateur;
    }

    public boolean isApprenticeAmateur() {
        return apprenticeAmateur;
    }

    public void setApprenticeAmateur(boolean apprenticeAmateur) {
        this.apprenticeAmateur = apprenticeAmateur;
    }

    public boolean isApprenticePro() {
        return apprenticePro;
    }

    public void setApprenticePro(boolean apprenticePro) {
        this.apprenticePro = apprenticePro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
