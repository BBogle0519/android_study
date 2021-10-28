package com.example.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);
        startLoading();
    }

    private void startLoading(){
        Handler hd = new Handler();
        hd.postDelayed(() -> {
            Intent intent = new Intent(getApplication(), MainPage.class);
            startActivity(intent);
        }, 3000);
    }
}