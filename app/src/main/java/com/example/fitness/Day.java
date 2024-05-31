package com.example.fitness;

public class Day {
    private String dayOfWeek;
    private String date;
    private boolean isToday;

    public Day(String dayOfWeek, String date, boolean isToday) {
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.isToday = isToday;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public boolean isToday() {
        return isToday;
    }
}