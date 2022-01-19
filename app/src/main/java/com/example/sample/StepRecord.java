package com.example.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepRecord extends BroadcastReceiver {
    ApiService service;
    int user_id_pk;
    int daily_step;
    double daily_distance;

    @Override
    public void onReceive(Context context, Intent intent) {
        service = RetrofitClient.getClient().create(ApiService.class);
        user_id_pk = intent.getIntExtra("user_id_pk", 0);
        daily_step = intent.getIntExtra("daily_step", 0);
        daily_distance = intent.getDoubleExtra("daily_distance", 0);

        saveRecord(new StepData(user_id_pk, daily_step, daily_distance));

        // Log.e("onReceive\n", "getID: " + user_id_pk);
        // Log.e("onReceive\n", "getSTEP: " + daily_step);
        Log.e("onReceive\n", "getDistance: " + daily_distance);
    }

    private void saveRecord(StepData data) {
        // Log.e("SaveRecord \n", "data: " + data);
        service.step_post(data).enqueue(new Callback<StepResponse>() {
            @Override
            public void onResponse(@NonNull Call<StepResponse> call, @NonNull Response<StepResponse> response) {
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
