package com.example.fitness;

import java.util.Objects;

public class FirebaseData {
    private String sampleName;
    private int sampleNumber;
    private boolean sampleBoolean;

    public FirebaseData(String sampleName, int sampleNumber, boolean sampleBoolean) {
        this.sampleName = sampleName;
        this.sampleNumber = sampleNumber;
        this.sampleBoolean = sampleBoolean;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public boolean isSampleBoolean() {
        return sampleBoolean;
    }

    public void setSampleBoolean(boolean sampleBoolean) {
        this.sampleBoolean = sampleBoolean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirebaseData that = (FirebaseData) o;
        return sampleNumber == that.sampleNumber &&
                sampleBoolean == that.sampleBoolean &&
                Objects.equals(sampleName, that.sampleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sampleName, sampleNumber, sampleBoolean);
    }

    @Override
    public String toString() {
        return "FirebaseData{" +
                "sampleName='" + sampleName + '\'' +
                ", sampleNumber=" + sampleNumber +
                ", sampleBoolean=" + sampleBoolean +
                '}';
    }
}
