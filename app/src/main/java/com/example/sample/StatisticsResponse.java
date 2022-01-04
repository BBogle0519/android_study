package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class StatisticsResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("year_data")
    private String year_data;

    @SerializedName("month_data")
    private String month_data;

    @SerializedName("day_data")
    private String day_data;

    public String getMessage() {
        return message;
    }

    public String getYear_data() {
        return year_data;
    }

    public String getMonth_data() {
        return month_data;
    }

    public String getDay_data() {
        return day_data;
    }
}
