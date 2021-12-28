package com.example.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepRecord extends BroadcastReceiver {
    ApiService service;
    int user_id_pk;
    int daily_step;

    @Override
    public void onReceive(Context context, Intent intent) {
        service = RetrofitClient.getClient().create(ApiService.class);
        user_id_pk = intent.getIntExtra("user_id_pk", 0);
        daily_step = intent.getIntExtra("daily_step", 0);
        stepRecord(new StepData(user_id_pk, daily_step));

        Log.e("onReceive\n", "getID: " + user_id_pk);
        Log.e("onReceive\n", "getSTEP: " + daily_step);
    }

    private void stepRecord(StepData data) {
        // Log.e("stepRecord \n", "data: " + data);
        service.step_post(data).enqueue(new Callback<StepResponse>() {
            @Override
            public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                StepResponse result = response.body();
                Log.e("onResponse\n", new Gson().toJson(result));
                Log.e("response.code()\n", String.valueOf(response.code()));

                if (response.isSuccessful()) {
                    Log.e("stepRecord 2xx\n", "response.code(): " + response.code());

                } else {
                    // 걸음수 저장 실패
                    Log.e("stepRecord 4xx\n", "response.code(): " + response.code());
                }

            }

            @Override
            public void onFailure(Call<StepResponse> call, Throwable t) {
                Log.e("stepRecord 5xx", t.getMessage());
            }
        });
    }
}
