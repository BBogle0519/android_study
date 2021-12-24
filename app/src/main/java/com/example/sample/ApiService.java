package com.example.sample;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/account/Join/")
    Call<RegistData> regist_post(@Body RegistData post);

    @POST("/account/Login/")
    Call<LoginResponse> login_post(@Body LoginData data);
}