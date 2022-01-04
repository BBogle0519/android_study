package com.example.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Statistics extends AppCompatActivity {
    ApiService service;
    int user_id_pk = 0;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        service = RetrofitClient.getClient().create(ApiService.class);

        Intent intent = getIntent();
        user_id_pk = intent.getIntExtra("user_id_pk", 0);
        //Log.e("Statistics\n", "user_id_pk: " + user_id_pk);
        getStatistics(new StatisticsData(user_id_pk));
    }

    private void getStatistics(StatisticsData data) {
        service.step_statistics(data).enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatisticsResponse> call, @NonNull Response<StatisticsResponse> response) {
                StatisticsResponse result = response.body();
                Log.e("onResponse\n", new Gson().toJson(result));
                Log.e("response.code()\n", String.valueOf(response.code()));

                if (response.isSuccessful()) {
                    Log.e("stepRecord 2xx\n", "response.code(): " + response.code());
                    String message = result.getMessage();
                    String year_data = result.getYear_data();
                    String month_data = result.getMonth_data();
                    String day_data = result.getDay_data();

                    tv1.findViewById(R.id.tv1);
                    tv2.findViewById(R.id.tv2);
                    tv3.findViewById(R.id.tv3);
                    tv4.findViewById(R.id.tv4);

                    tv1.setText(message);
                    tv2.setText(year_data);
                    tv3.setText(month_data);
                    tv4.setText(day_data);

                } else {
                    // 저장된 데이터 없음
                    Log.e("stepRecord 4xx\n", "response.code(): " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Log.e("stepRecord 5xx", t.getMessage());
            }
        });
    }
}