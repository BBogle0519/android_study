package com.example.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText edt_id;
    EditText edt_pw;
    Button btn_ok;
    Button btn_cancel;
    Button btn_regist;
    ApiService service;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edt_id = findViewById(R.id.login_edt_id);
        edt_pw = findViewById(R.id.login_edt_pw);
        btn_ok = findViewById(R.id.login_btn_ok);
        btn_cancel = findViewById(R.id.login_btn_cancel);
        btn_regist = findViewById(R.id.login_btn_regist);
        service = RetrofitClient.getClient().create(ApiService.class);

        btn_ok.setOnClickListener(view -> attemptLogin());

        btn_cancel.setOnClickListener(view -> finish());

        btn_regist.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Regist.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        edt_id.setError(null);
        edt_pw.setError(null);

        String id = edt_id.getText().toString();
        String pw = edt_pw.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //아이디 유효성 검사
        if (id.isEmpty()) {
            edt_id.setError("아이디를 입력해주세요.");
            focusView = edt_id;
            cancel = true;
        }

        //패스워드 유효성 검사
        if (pw.isEmpty()) {
            edt_pw.setError("8자 이상의 비밀번호를 입력해주세요.");
            focusView = edt_pw;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startLogin(new LoginData(id, pw));
        }
    }

    private void startLogin(LoginData data) {
        service.login_post(data).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginResponse> call, @NotNull Response<LoginResponse> response) {
                LoginResponse result = response.body();
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Log.e("startLogin", t.getMessage());
            }
        });
    }
}
