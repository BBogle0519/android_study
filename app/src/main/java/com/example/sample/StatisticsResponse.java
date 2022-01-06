package com.example.sample;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StatisticsResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("year_data")
    private List<StatisticsItem> year_data;

    @SerializedName("month_data")
    private List<StatisticsItem> month_data;

    @SerializedName("day_data")
    private List<StatisticsItem> day_data;

    public String getMessage() {
        return message;
    }

    public List<StatisticsItem> getYear_data() {
        return year_data;
    }

    public List<StatisticsItem> getMonth_data() {
        return month_data;
    }

    public List<StatisticsItem> getDay_data() {
        return day_data;
    }
}
