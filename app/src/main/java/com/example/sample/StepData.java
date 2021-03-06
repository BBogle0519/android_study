package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class StepData {
    @SerializedName("user_id_pk")
    public int user_id_pk;

    @SerializedName("step")
    public int step;

    @SerializedName("distance")
    public double distance;

//    @SerializedName("record")
//    public String record;

    public StepData(int user_id_pk, int step, double distance) {
        this.user_id_pk = user_id_pk;
        this.step = step;
        this.distance = distance;
//        this.record = record;
    }
}
