
package com.example.sample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainPage extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    //각각 객체 생성
    Toolbar toolbar;    //툴바
    DrawerLayout mDrawerLayout;
    Context context = this;
    NavigationView navigationView;
    TextView currentView;    // 현재 걸음 수 텍스트뷰
    TextView totalView;    // 총 걸음 수 텍스트 뷰
    Button course;      //코스추천으로 이동 (임시)
    ProgressBar progressBar; //프로그래스바 (목표 걸음수)
    GoogleMap mMap;     //지도 (추후에 네이버 지도랑 비교하여 유리한것 사용하기)
    SensorManager sensorManager;
    Sensor stepCountSensor; //TYPE_STEP_COUNTER, TYPE_STEP_DETECTOR 센서 두 종류 중 전자 선택 (앱 종료중에도 측정하기 위함)

    int counterStep = 0;    // 센서에 누적된 총 걸음 수
    int currentStep = 0;    // 현재 걸음 수

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        progressBar = findViewById(R.id.progressBar);
        currentView = findViewById(R.id.current_step);
        totalView = findViewById(R.id.total_step);
        course = findViewById(R.id.btn2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);

        course.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "코스추천 호출", Toast.LENGTH_SHORT).show());

        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCountSensor == null) { // 걸음수 측정 센서가 없는 경우 출력
            // AVD 에선 센서가 없으므로 임의값으로 테스트
            Intent resetIntent = new Intent(context, StepRecord.class);
            currentStep = 5000; // test value
            resetIntent.putExtra("daily_step", currentStep);
            sendBroadcast(resetIntent);

            Log.e("stepCountSensor", "걸음 측정 센서 없음.");
        }

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);              // 메뉴 버튼 활성화
        actionBar.setDisplayShowTitleEnabled(false);            // 기존 타이틀 삭제
        actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground); // 메뉴 버튼 이미지 수정하기 임시
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //네비게이션 헤더 설정 및 동적 요소 추가
        LinearLayout navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navi_header, null);
        navigation_container.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
        navigation_container.setPadding(30, 70, 30, 50);
        navigation_container.setOrientation(LinearLayout.VERTICAL);
        navigation_container.setGravity(Gravity.BOTTOM);
        navigation_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //네비게이션 헤더 텍스트뷰에 커스텀 폰트 적용
        //Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/nanumbarungothicbold.ttf");
        TextView tv_username = new TextView(this);
        tv_username.setTextColor(getResources().getColor(R.color.colorAccent));
        tv_username.setTextSize(17);
        //tv_username.setTypeface(typeface);

        if (TextUtils.isEmpty(tv_username.getText())) {
            tv_username.setText("로그인이 필요합니다.");
        } else {
            Intent intent = getIntent();
            RegistData reg_info = (RegistData) intent.getSerializableExtra("reg_info");
            tv_username.setText(reg_info.getUser_id() + " 님");
        }
        navigation_container.addView(tv_username);

        //헤더 적용
        navigationView.addHeaderView(navigation_container);

        // 네비게이션 계정 메뉴 터치시
        navigationView.setNavigationItemSelectedListener(menuItem -> { // 네비게이션의 버튼 클릭 시 이벤트
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();

            int id = menuItem.getItemId();
            String title = menuItem.getTitle().toString();

            if (id == R.id.account) {
                Intent intent = new Intent(getApplication(), Regist.class);
                startActivity(intent);
                Toast.makeText(context, title + ": 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.setting) { //네비게이션 설정 메뉴 터치시
                Toast.makeText(context, title + ": 설정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.logout) { //네비게이션 로그아웃 메뉴 터치시
                Toast.makeText(context, title + ": 로그아웃 시도중", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

    } // onCreate();

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// 툴바의 버튼 클릭 시 이벤트
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() { // 어플리케이션 일시중지 상태
        super.onPause();
        //센서 작동 중지
        //sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() { // 어플리케이션 활성화 상태
        super.onResume();
        //센서 재작동
        //sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {  // 센서가 동작을 감지하면 onSensorChanged() 함수로 전달
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //stepcountsenersor는 앱이 꺼지더라도 초기화 되지않는다. 그러므로 우리는 초기값을 가지고 있어야한다.
            if (counterStep < 1) {
                // initial value
                counterStep = (int) sensorEvent.values[0];
            }
            //총 누적 걸음 - 리셋 안된 값 (앱 실행부터 초기화 한번도 안하면 총 누적걸음이 현재 걸음)
            currentStep = (int) sensorEvent.values[0] - counterStep;

            // 당일 걸음 수, 누적 걸음 수 표시
            currentView.setText(String.valueOf(currentStep));
            totalView.setText((int) sensorEvent.values[0]);

            // 프로그래스바에 표시
            progressBar.setProgress(currentStep);

            // 24시간(00:00 ~ 24:00) 측정하여 00:00시 마다 현재 걸음 수 0으로 표시, DB에 값 저장 (후에 확장하여 이동거리, 소비 칼로리 등 정보 추가하여 저장)
            // 정시마다 실행되는 서비스엔 Timer보다 AlarmManager가 적합. (https://greedy0110.tistory.com/69)
            // AlarmManager TEST
            AlarmManager resetAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // StepRecord에 측정된 하루치 걸음 수 전송
            Intent resetIntent = new Intent(context, StepRecord.class);
            resetIntent.putExtra("daily_step", currentStep);

            // PendingIntent(보류 인텐트)를 사용하여 지정한 시간에 intent 실행
            PendingIntent resetSender = PendingIntent.getBroadcast(context, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // AlarmManager.setInexactRepeating 반복적으로 지정한 시간에 작업
            resetAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, resetSender);

            SimpleDateFormat format = new SimpleDateFormat("MM/dd kk:mm:ss");
            String setResetTime = format.format(new Date(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY));
            Log.e("resetAlarm", "ResetHour : " + setResetTime);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { // 가속도 센서

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) { // 지도 설정
        mMap = googleMap;
        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions.position(SEOUL);
        makerOptions.title("서울");
        makerOptions.snippet("한국의 수도");
        mMap.addMarker(makerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));
    }
}