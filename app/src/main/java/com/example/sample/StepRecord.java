package com.example.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepRecord extends BroadcastReceiver {
    ApiService service;
    int daily_step = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        service = RetrofitClient.getClient().create(ApiService.class);
        daily_step = intent.getIntExtra("daily_step", 0);
        stepRecord(new StepData(daily_step));

        // Log.e("onReceive\n", "getIntExtra: " + daily_step);
    }

    private void stepRecord(StepData data) {
        // Log.e("stepRecord \n", "data: " + data);
        service.step_post(data).enqueue(new Callback<StepResponse>() {
            @Override
            public void onResponse(Call<StepResponse> call, Response<StepResponse> response) {
                StepResponse result = response.body();
                // Log.e("onResponse\n", new Gson().toJson(result));

                if (response.code() == 200) {
                    Log.e("stepRecord 200\n", "getStatus: " + result.getStatus());

                } else if (response.code() == 404) {
                    // 걸음수 저장 실패
                    Log.e("stepRecord 404\n", "response.code(): " + response.code());
                }

            }

            @Override
            public void onFailure(Call<StepResponse> call, Throwable t) {
                Log.e("stepRecord 5xx", t.getMessage());
            }
        });
    }
}
