package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class StepData {
    @SerializedName("step")
    public int step;

//    @SerializedName("record")
//    public String record;

    public StepData(int step) {
        this.step = step;
//        this.record = record;
    }
}
