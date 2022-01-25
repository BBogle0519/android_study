package com.example.sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Regist extends AppCompatActivity {
    Button btn_ok;      // 확인버튼
    Button btn_cnl;     // 취소버튼

    EditText edt_id;    // 아이디입력
    EditText edt_pw;    // 패스워드입력
    EditText edt_nm;    // 이름입력
    EditText edt_tall;  // 키입력
    EditText edt_ph;    // 전화번호입력
    EditText edt_mail;  // 메일입력

    RadioGroup radio_group;

    private ApiService service; // apiservice 객체 생성

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);

        // retrofit2 api
        service = RetrofitClient.getClient().create(ApiService.class);

        btn_ok = findViewById(R.id.btn_reg_ok);
        btn_cnl = findViewById(R.id.btn_reg_cnl);

        edt_id = findViewById(R.id.edt_id);
        edt_pw = findViewById(R.id.edt_pw);
        edt_nm = findViewById(R.id.edt_nm);
        edt_tall = findViewById(R.id.edt_tall);
        edt_ph = findViewById(R.id.edt_ph);
        edt_mail = findViewById(R.id.edt_mail);

        radio_group = findViewById(R.id.radio_group);

        final int[] sex = new int[1];
        radio_group.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.radio_male:
                    sex[0] = 0;
                    break;
                case R.id.radio_female:
                    sex[0] = 1;
                    break;
            }
        });

        // 확인버튼 터치시 동작
        btn_ok.setOnClickListener(view -> {
            //Log.e("btn_ok", "attemptRegist 호출");
            attemptRegist(sex);
        });

        // 취소버튼시 액티비티 종료
        btn_cnl.setOnClickListener(view -> finish());
    }

    private void attemptRegist(int[] sex) { // 사용자 입력데이터 유효성 검사
        edt_id.setError(null);
        edt_pw.setError(null);
        edt_nm.setError(null);
        edt_ph.setError(null);
        edt_tall.setError(null);
        edt_mail.setError(null);

        String id = edt_id.getText().toString();
        String pw = edt_pw.getText().toString();
        String nm = edt_nm.getText().toString();
        String tall = edt_tall.getText().toString();
        String ph = edt_ph.getText().toString();
        String mail = edt_mail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //id 유효성 검사
        if (id.isEmpty()) {
            edt_id.setError("아이디를 입력해주세요");
            focusView = edt_id;
            cancel = true;
        } else if (!isIdValid(id)) {
            edt_id.setError("5~20자 이내로 아이디를 입력해주세요.");
            focusView = edt_id;
            cancel = true;
        }

        //pw 유효성 검사
        if (pw.isEmpty()) {
            edt_pw.setError("비밀번호를 입력해주세요.");
            focusView = edt_pw;
            cancel = true;
        } else if (!isPwValid(pw)) {
            edt_pw.setError("8자 이내로 비밀번호를 입력해주세요.");
            focusView = edt_pw;
            cancel = true;
        }

        //nm 유효성 검사
        if (nm.isEmpty()) {
            edt_nm.setError("이름을 입력해주세요.");
            focusView = edt_nm;
            cancel = true;
        }

        //ph 유효성 검사
        if (ph.isEmpty()) {
            edt_ph.setError("전화번호를 입력해주세요.");
            focusView = edt_ph;
            cancel = true;
        }

        //tall 유효성 검사
        if (tall.isEmpty()) {
            edt_tall.setError("키를 입력해주세요.");
            focusView = edt_tall;
            cancel = true;
        }

        //mail 유효성 검사
        if (mail.isEmpty()) {
            edt_mail.setError("이메일을 입력해주세요.");
            focusView = edt_mail;
            cancel = true;
        } else if (!isEmailValid(mail)) {
            edt_mail.setError("올바른 메일주소를 입력해주세요.");
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            startRegist(new RegistData(id, pw, nm, Integer.parseInt(ph), sex[0], Integer.parseInt(tall), mail));
        }
    }

    private void startRegist(RegistData data) { // 서버로 데이터 전송 및 응답받기
        service.regist_post(data).enqueue(new Callback<RegistData>() {
            @Override
            public void onResponse(@NotNull Call<RegistData> call, @NotNull Response<RegistData> response) {
                //Log.e("startRegist", " onResponse 접근");
                //Log.e("regist", response.message());
                //Log.e("regist, String.valueOf(response.code()));

                if (response.isSuccessful()) {
                    // 회원가입 성공시 로그인 화면 출력
                    RegistData result = response.body();
                    Log.e("regist: 2xx\n", new Gson().toJson(result));
                } else {
                    // 회원가입 실패시 회원가입 화면 재출력
                    Log.e("regist: 4xx\n", String.valueOf(response.code()));
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
                Log.e("regist: 5xx\n", t.getMessage());
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