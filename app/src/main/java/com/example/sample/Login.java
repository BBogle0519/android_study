package com.example.sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    //    Button btn_cancel;
    Button btn_regist;
    ApiService service;
    Context context = this;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edt_id = findViewById(R.id.login_edt_id);
        edt_pw = findViewById(R.id.login_edt_pw);
        btn_ok = findViewById(R.id.login_btn_ok);
//        btn_cancel = findViewById(R.id.login_btn_cancel);
        btn_regist = findViewById(R.id.login_btn_regist);
        service = RetrofitClient.getClient().create(ApiService.class);

        btn_ok.setOnClickListener(view -> {
            // 로그인 처리 attemptLogin() 호출
            attemptLogin();
            //Intent intent = new Intent(getApplicationContext(), MainPage.class);
            //startActivity(intent);
            //finish();
        });

//        btn_cancel.setOnClickListener(view -> finish());

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
                // Log.e("onResponse\n", new Gson().toJson(result));
                if (response.isSuccessful()) {
                    // Log.e("Login: 2xx\n", "getAccess: " + String.valueOf(result.getAccess()));
                    // Log.e("Login: 2xx\n", "getRefresh: " + String.valueOf(result.getRefresh()));

                    // SharedPreferences로 토큰 저장
                    // 저장 경로: data/data/패키지명/shared_prefs/SharedPreference명.xml

                    int user_id_pk = result.getId();
                    String access_token = result.getAccess();
                    String refresh_token = result.getRefresh();
                    Log.e("Login\n", "user_id_pk: " + user_id_pk);

                    SharedPreferences prefrences = getSharedPreferences("token", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefrences.edit();

                    // 임시로 user_pk 저장했는데 보안상 저장하지말고 서버단에서 토큰으로 정보 검색하여 처리하는게 맞는듯하니 확인하고 후에 수정
                    // SharedPreferences 저장된 파일은 관리자 권한이 있으면 들어갈 수 있으나 정보를 암호화하여 저장한다면 보안이슈 해결 가능
                    editor.putInt("user_id_pk", user_id_pk);
                    editor.putString("access", access_token);
                    editor.putString("refresh", refresh_token);
                    editor.commit();
                    Log.e("Login: 2xx\n", "토큰 저장 완료");

                    // 로그인한 사용자의 화면으로 변경
                    Intent intent = new Intent(getApplicationContext(), MainPage.class);
                    intent.putExtra("login_id", data.getUser_id());
                    startActivity(intent);
                    //finish();

                } else {
                    // 일치하는 아이디, 비밀번호 확인하도록 처리
                    Log.e("Login: 4xx\n", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<LoginResponse> call, @NotNull Throwable t) {
                Toast.makeText(context, "서버 연결 실패: 5xx", Toast.LENGTH_SHORT).show();
                Log.e("Login: 5xx", t.getMessage());
            }
        });
    }
}
