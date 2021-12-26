package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class StepResponse {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}
