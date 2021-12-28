package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private String status;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("password")
    private String password;

    @SerializedName("access")
    private String access;

    @SerializedName("refresh")
    private String refresh;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPassword() {
        return password;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }
}
