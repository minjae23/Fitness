package com.example.fitness;

import java.util.ArrayList;

public class Plan {
    private String weekly;
    private String exerPartArray;

    public Plan(String weekly, String exerPartArray) {
        this.weekly=weekly;
        this.exerPartArray = exerPartArray;
    }

    public String getWeekly(){ return weekly; }

    public String getExerPartArray() {
        return exerPartArray;
    }
}