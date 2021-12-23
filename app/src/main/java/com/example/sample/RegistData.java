package com.example.sample;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class RegistData implements Serializable {
    @SerializedName("user_id")
    private String user_id;

    @SerializedName("user_pw")
    private String user_pw;

    @SerializedName("user_nm")
    private String user_nm;

    @SerializedName("user_ph")
    private int user_ph;

    @SerializedName("user_email")
    private String user_email;

//    @SerializedName("user_reg_date")  회원가입일 (장고에서 자동생성)
//    private Timestamp user_reg_date;

//    @SerializedName("user_st")        회원상태 (장고에서 자동생성)
//    private int user_st;

    public RegistData(String user_id, String user_pw, String user_nm, int user_ph, String user_email) {
        this.user_id = user_id;
        this.user_pw = user_pw;
        this.user_nm = user_nm;
        this.user_ph = user_ph;
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_pw() {
        return user_pw;
    }

    public void setUser_pw(String user_pw) {
        this.user_pw = user_pw;
    }

    public String getUser_nm() {
        return user_nm;
    }

    public void setUser_nm(String user_nm) {
        this.user_nm = user_nm;
    }

    public int getUser_ph() {
        return user_ph;
    }

    public void setUser_ph(int user_ph) {
        this.user_ph = user_ph;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
