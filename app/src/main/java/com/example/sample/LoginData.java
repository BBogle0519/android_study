package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("user_id")
    String user_id;

    @SerializedName("user_pw")
    String user_pw;

    public LoginData(String user_id, String user_pw) {
        this.user_id = user_id;
        this.user_pw = user_pw;
    }
}
