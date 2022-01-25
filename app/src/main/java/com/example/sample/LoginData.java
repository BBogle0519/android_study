package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("user_id")
    public String user_id;

    @SerializedName("password")
    public String password;

    public LoginData(String user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }
}
