package com.example.sample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Statistics extends AppCompatActivity {
    ApiService service;
    int user_id_pk = 0;

    BarChart barchart;
    ArrayList<String> label = new ArrayList<>();
    ArrayList<Integer> chart_data = new ArrayList<>();

    ArrayList<String> year = new ArrayList<>();
    ArrayList<String> month = new ArrayList<>();
    ArrayList<String> day = new ArrayList<>();
    ArrayList<Integer> step = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        service = RetrofitClient.getClient().create(ApiService.class);

        Intent intent = getIntent();
        user_id_pk = intent.getIntExtra("user_id_pk", 0);
        //Log.e("Statistics\n", "user_id_pk: " + user_id_pk);
        getStatistics(new StatisticsData(user_id_pk));

        // 막대 그래프
        barchart = findViewById(R.id.barchart);
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
                    List<StatisticsItem> year_data = result.getYear_data();
                    List<StatisticsItem> month_data = result.getMonth_data();
                    List<StatisticsItem> day_data = result.getDay_data();

//                    if (사용자선택 == 연도별) {
//
//                    } else if (사용자선택 == 월별) {
//
//                    } else if (사용자선택 == 일별) {
//
//                    }

                    for (int i = 0; i < year_data.size(); i++) {
                        label.add(i, year_data.get(i).getYear());
                        chart_data.add(i, year_data.get(i).getStep());
                    }
                    // 그래프 세팅
                    setBarchart(label, chart_data);

//                    for (int i = 0; i < month_data.size(); i++) {
//                        year.add(i, month_data.get(i).getYear());
//                        month.add(i, month_data.get(i).getMonth());
//                        step.add(i, month_data.get(i).getStep());
//                    }
//
//                    for (int i = 0; i < day_data.size(); i++) {
//                        year.add(i, day_data.get(i).getYear());
//                        month.add(i, day_data.get(i).getMonth());
//                        day.add(i, day_data.get(i).getDay());
//                        step.add(i, day_data.get(i).getStep());
//                    }

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

    public void setBarchart(ArrayList<String> label_list, ArrayList<Integer> data_list) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < data_list.size(); i++) {
            entries.add(new BarEntry(Integer.parseInt(label_list.get(i)), data_list.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "steps");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.3f);
        
        // 그래프 설정
        barchart.setMaxVisibleValueCount(7);
        barchart.setFitBars(true);
        barchart.setData(data);
        barchart.getDescription().setText("year step count");
        barchart.animateY(2000);
        barchart.setTouchEnabled(false);

        // x축
        barchart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barchart.getXAxis().setGranularity(1f);

        // y축
        barchart.getAxisLeft().setDrawLabels(false);
        barchart.getAxisLeft().setDrawAxisLine(false);
        barchart.getAxisLeft().setDrawGridLines(false);

    }

}