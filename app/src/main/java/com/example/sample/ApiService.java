package com.example.sample;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/apps/Join/")
    Call<RegistData> regist_post(@Body RegistData post);

    @POST("/apps/Login/")
    Call<LoginResponse> login_post(@Body LoginData data);
}