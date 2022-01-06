package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class StatisticsItem {
    @SerializedName("year")
    private String year;

    @SerializedName("month")
    private String month;

    @SerializedName("day")
    private String day;

    @SerializedName("step")
    private int step;

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public int getStep() {
        return step;
    }
}
