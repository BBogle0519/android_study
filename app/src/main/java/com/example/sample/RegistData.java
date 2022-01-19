package com.example.sample;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class RegistData implements Serializable {
    @SerializedName("user_id")
    private String user_id;

    @SerializedName("password")
    private String password;

    @SerializedName("user_nm")
    private String user_nm;

    @SerializedName("user_ph")
    private int user_ph;

    @SerializedName("user_sex")
    private int user_sex;

    @SerializedName("user_tall")
    private int user_tall;

    @SerializedName("user_email")
    private String user_email;

//    @SerializedName("user_reg_date")  회원가입일 (장고에서 자동생성)
//    private Timestamp user_reg_date;

//    @SerializedName("user_st")        회원상태 (장고에서 자동생성)
//    private int user_st;

    public RegistData(String user_id, String password, String user_nm, int user_ph, int user_sex, int user_tall, String user_email) {
        this.user_id = user_id;
        this.password = password;
        this.user_nm = user_nm;
        this.user_sex = user_sex;
        this.user_tall = user_tall;
        this.user_ph = user_ph;
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_nm() {
        return user_nm;
    }

    public void setUser_nm(String user_nm) {
        this.user_nm = user_nm;
    }

    public int getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(int user_sex) {
        this.user_sex = user_sex;
    }

    public int getUser_tall() {
        return user_tall;
    }

    public void setUser_tall(int user_tall) {
        this.user_tall = user_tall;
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
