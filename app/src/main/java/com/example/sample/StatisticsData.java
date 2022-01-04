package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class StatisticsData {
    @SerializedName("user_id_pk")
    public int user_id_pk;

    public StatisticsData(int user_id_pk) {
        this.user_id_pk = user_id_pk;
    }

}
