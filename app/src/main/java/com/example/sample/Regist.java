package com.example.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Regist extends AppCompatActivity {
    //객체 생성
    private Button btn_ok;      // 확인버튼
    private Button btn_cnl;     // 취소버튼

    //추후에 AutoCompleteTextView 적용하기
    private EditText edt_id;    // 아이디입력
    private EditText edt_pw;    // 패스워드입력
    private EditText edt_nm;    // 이름입력
    private EditText edt_ph;    // 전화번호입력
    private EditText edt_mail;  // 메일입력

    private AutoCompleteTextView atv_id; // 아이디 입력오류 출력
    private AutoCompleteTextView atv_pw; // 패스워드 입력오류 출력
    private AutoCompleteTextView atv_nm; // 이름 입력오류 출력
    private AutoCompleteTextView atv_ph; // 전화번호 입력오류 출력
    private AutoCompleteTextView atv_mail; // 메일 입력오류 출력

    private ApiService service; // apiservice 객체 생성

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);

        btn_ok = findViewById(R.id.btn_reg_ok);
        btn_cnl = findViewById(R.id.btn_reg_cnl);

        edt_id = findViewById(R.id.edt_id);
        edt_pw = findViewById(R.id.edt_pw);
        edt_nm = findViewById(R.id.edt_nm);
        edt_ph = findViewById(R.id.edt_ph);
        edt_mail = findViewById(R.id.edt_mail);

        atv_id = findViewById(R.id.atv_id);
        atv_pw = findViewById(R.id.atv_pw);
        atv_nm = findViewById(R.id.atv_nm);
        atv_ph = findViewById(R.id.atv_ph);
        atv_mail = findViewById(R.id.atv_mail);

        // retrofit2 api
        service = RetrofitClient.getClient().create(ApiService.class);

        // 확인버튼 터치시 동작
        btn_ok.setOnClickListener(view -> {
            //Log.e("btn_ok", "attemptRegist 접근");
            attemptRegist();
            //Log.e("btn_ok", "attemptRegist 호출");
        });

        // 취소버튼시 액티비티 종료
        btn_cnl.setOnClickListener(view -> finish());
    }

    private void attemptRegist() { // 사용자 입력데이터 유효성 검사
        atv_id.setError(null);
        atv_pw.setError(null);
        atv_nm.setError(null);
        atv_ph.setError(null);
        atv_mail.setError(null);

        String id = edt_id.getText().toString();
        String pw = edt_pw.getText().toString();
        String nm = edt_nm.getText().toString();
        String ph = edt_ph.getText().toString();
        String mail = edt_mail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //id 유효성 검사
        if (id.isEmpty()) {
            atv_id.setError("아이디를 입력해주세요");
            focusView = edt_id;
            cancel = true;
        } else if (!isIdValid(id)) {
            atv_id.setError("5~20자 이내로 아이디를 입력해주세요.");
            focusView = edt_id;
            cancel = true;
        }

        //pw 유효성 검사
        if (pw.isEmpty()) {
            atv_pw.setError("비밀번호를 입력해주세요.");
            focusView = edt_pw;
            cancel = true;
        } else if (!isPwValid(pw)) {
            atv_pw.setError("8자 이내로 비밀번호를 입력해주세요.");
            focusView = edt_pw;
            cancel = true;
        }

        //nm 유효성 검사
        if (nm.isEmpty()) {
            atv_nm.setError("이름을 입력해주세요.");
            focusView = edt_nm;
            cancel = true;
        }

        //ph 유효성 검사
        if (ph.isEmpty()) {
            atv_ph.setError("전화번호를 입력해주세요.");
            focusView = edt_ph;
            cancel = true;
        }

        //mail 유효성 검사
        if (mail.isEmpty()) {
            atv_mail.setError("이메일을 입력해주세요.");
            focusView = edt_mail;
            cancel = true;
        } else if (!isEmailValid(mail)) {
            atv_mail.setError("올바른 메일주소를 입력해주세요.");
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startRegist(new RegistData(id, pw, nm, Integer.parseInt(ph), mail));
        }
    }

    private void startRegist(RegistData data) { // 서버로 데이터 전송 및 응답받기
        service.regist_post(data).enqueue(new Callback<RegistData>() {
            @Override
            public void onResponse(@NotNull Call<RegistData> call, @NotNull Response<RegistData> response) {
                Log.e("startRegist", " onResponse 접근");
                Log.e("response.getMessage", response.message());
                Log.e("response.getCode()", String.valueOf(response.code()));

                if (response.isSuccessful()) {
                    // 회원가입 성공시 로그인 화면 출력
                    RegistData result = response.body();
                    Log.e("onResponse: 가입 성공, 결과\n", new Gson().toJson(result));
                } else {
                    // 회원가입 실패시 회원가입 화면 재출력
                    Log.e("onResponse: \n", "가입 실패");
                    Intent intent = new Intent(getApplicationContext(), Regist.class);
                    startActivity(intent);
                }
                finish();

                // 로그인화면으로 이동
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(@NotNull Call<RegistData> call, @NotNull Throwable t) {
                //Toast.makeText(Regist.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }

    private void naviLogin(RegistData data) {
        Log.e("naviLogin: \n", data.getUser_id() + " " + data.getUser_nm());
    }

    private boolean isIdValid(String id) {  //아이디 글자 수 제한
        return id.length() >= 5 && id.length() <= 20;
    }

    private boolean isPwValid(String pw) {  //비밀번호 글자 수 제한
        return pw.length() >= 8;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
}