package com.example.sample;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("user_no")
    private String user_no;

    @SerializedName("user_id")
    private String user_id;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_no() {
        return user_no;
    }
}
