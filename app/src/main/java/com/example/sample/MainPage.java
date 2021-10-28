
package com.example.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class MainPage extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    //각각 객체 생성
    Toolbar toolbar;    //툴바
    DrawerLayout mDrawerLayout;
    Context context = this;
    NavigationView navigationView;
    TextView stepCount;    //현재 걸음수
    Button course;      //코스추천으로 이동 (임시)
    ProgressBar progressBar; //프로그래스바 (목표 걸음수)
    GoogleMap mMap;     //지도 (추후에 네이버 지도랑 비교하여 유리한것 사용하기)
    SensorManager sensorManager;
    Sensor stepCountSensor;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        progressBar = findViewById(R.id.progressBar);
        stepCount = findViewById(R.id.text2);
        course = findViewById(R.id.btn2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);

        course.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "코스추천 호출", Toast.LENGTH_SHORT).show());

        mapFragment.getMapAsync(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCountSensor == null) { // 걸음수 측정 센서가 없는 경우 출력
            Toast.makeText(this, "No Detect step sensor", Toast.LENGTH_LONG).show();
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
        LinearLayout navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navi_header,null);
        navigation_container.setBackground(getResources().getDrawable(R.color.colorPrimaryDark));
        navigation_container.setPadding(30,70,30,50);
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

        tv_username.setText("테스트중");

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
            stepCount.setText(String.valueOf(sensorEvent.values[0]));

            // 나중에 캐스팅(int) 사용 해도 되는지 확인하기
            progressBar.setProgress(Integer.parseInt(String.valueOf(sensorEvent.values[0])));
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