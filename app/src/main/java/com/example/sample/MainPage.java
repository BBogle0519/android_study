
package com.example.sample;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainPage extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    Toolbar toolbar;    //툴바
    DrawerLayout mDrawerLayout;
    Context context = this;
    NavigationView navigationView;
    TextView currentView;    // 현재 걸음 수 텍스트뷰
    TextView totalView;    // 총 걸음 수 텍스트 뷰
    Button course;      // 코스추천으로 이동 (임시)
    ProgressBar progressBar; // 프로그래스바 (목표 걸음수)
    GoogleMap mMap;     // 구글맵
    Marker currentMarker = null; //현재위치 마커
    SensorManager sensorManager;
    Sensor stepCountSensor; // TYPE_STEP_COUNTER, TYPE_STEP_DETECTOR 센서 두 종류 중 전자 선택 (앱 종료중에도 측정하기 위함)
    View main_layout;   // snackbar 사용 위한 view

    int counterStep = 0;    // 센서에 누적된 총 걸음 수
    int currentStep = 0;    // 현재 걸음 수
    double daily_distance = 0;
    ArrayList<Double> distance_sum = new ArrayList<>();

    // ActivityCompat.requestPermissions 퍼미션 요청 구별 위한 값
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Location currentLocation;
    LatLng currentPosition;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private LatLng startLatLng = new LatLng(0, 0);
    private LatLng endLatLng = new LatLng(0, 0);
    List<Polyline> polyLine = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        main_layout = findViewById(R.id.drawer_layout);
        progressBar = findViewById(R.id.progressBar);
        currentView = findViewById(R.id.current_step);
        totalView = findViewById(R.id.total_step);

        course = findViewById(R.id.btn2);
        course.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "코스추천 호출", Toast.LENGTH_SHORT).show());

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000) // 1초
                .setFastestInterval(500); // 0.5초
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        int activityRecognitionCheck = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION);

        // 활동 퍼미션 있는지 체크
        if (activityRecognitionCheck == PackageManager.PERMISSION_GRANTED) {
            // 1. 이미 퍼미션 허가 되있다면
            Toast.makeText(context, "활동퍼미션 이미 있음", Toast.LENGTH_SHORT).show();
        } else {
            // 2. 퍼미션 허가 안해놨다면 권한 요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                // 2.1 퍼미션 허가를 거부 한 적이 있는 경우 확인 메세지 띄우고 권한 요청
                Snackbar.make(main_layout, "이 앱을 실행하려면 활동권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainPage.this,
                                new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 2.2 퍼미션 허가를 거부 한 적이 없는 경우 바로 권한 요청
                ActivityCompat.requestPermissions(MainPage.this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSIONS_REQUEST_CODE);
            }
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Toast.makeText(context, "걸음 측정 센서 없음." + stepCountSensor, Toast.LENGTH_SHORT).show();
        Log.d("onCreate", "stepCountSensor: " + stepCountSensor);

        if (stepCountSensor == null) { // 걸음수 측정 센서가 없는 경우 출력
            // AVD 에선 센서가 없으므로 임의값으로 테스트
            SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
            int user_id_pk = pref.getInt("user_id_pk", 0);
            Log.e("stepCountSensor", "user_id_pk:" + user_id_pk);

            Intent resetIntent = new Intent(context, StepRecord.class);
            int testStep = 5000; // test value
            double testDistance = 102.22; // test value

            resetIntent.putExtra("daily_step", testStep);
            resetIntent.putExtra("daily_distance", testDistance);
            resetIntent.putExtra("user_id_pk", user_id_pk);
            sendBroadcast(resetIntent);

            // 당일 걸음 수, 누적 걸음 수 표시
            currentView.setText(String.valueOf(testStep));
            totalView.setText(String.valueOf(testStep) + "m");

            // 프로그래스바에 표시
            progressBar.setProgress(testStep);

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

            } else if (id == R.id.step_record) { //네비게이션 기록 통계 메뉴 터치시
                SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
                int user_id_pk = pref.getInt("user_id_pk", 0);
                Intent intent = new Intent(getApplication(), Statistics.class);
                intent.putExtra("user_id_pk", user_id_pk);
                startActivity(intent);

            } else if (id == R.id.logout) { //네비게이션 로그아웃 메뉴 터치시
                Toast.makeText(context, title + ": 로그아웃 시도중", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

        dailyRecord();

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
        if (item.getItemId() == android.R.id.home) { // 툴바의 버튼 클릭 시 이벤트
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
    public void onSensorChanged(SensorEvent sensorEvent) {  // 센서가 동작을 감지하면 onSensorChanged() 함수로 값 전달
        Log.e("onSensorChanged", "걸음 측정중.");
        Toast.makeText(this, "onSensorChanged() 호출됨", Toast.LENGTH_LONG).show();
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //stepcountsenersor는 앱이 꺼지더라도 초기화 되지않는다. 그러므로 초기값을 가지고 있어야한다.
            if (counterStep < 1) {
                // initial value
                counterStep = (int) sensorEvent.values[0];
            }
            //총 누적 걸음 - 리셋 안된 값 (앱 실행부터 초기화 한번도 안하면 총 누적걸음이 현재 걸음)
            currentStep = (int) sensorEvent.values[0] - counterStep;

            // 당일 걸음 수, 누적 걸음 수 표시
            currentView.setText(String.valueOf(currentStep));
            totalView.setText((int) sensorEvent.values[0] + "m");

            // 프로그래스바에 표시
            progressBar.setProgress(currentStep);
        }
    }

    // 24시간(00:00 ~ 24:00) 측정하여 00:00시 마다 현재 걸음 수 0으로 표시, DB에 데이터 저장
    public void dailyRecord() {
        // 정시마다 실행되는 서비스엔 Timer보다 AlarmManager가 적합. (https://greedy0110.tistory.com/69)
        // AlarmManager TEST
        AlarmManager resetAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // StepRecord에 user_id_pk, 측정된 하루치 걸음 수, 이동거리 전송
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        int user_id_pk = pref.getInt("user_id_pk", 0);

        if (distance_sum.size() != 0) {
            for (int i = 0; i < distance_sum.size(); i++) {
                double sum = 0;
                sum += distance_sum.get(i);
                daily_distance = sum;
            }
        }

        Intent resetIntent = new Intent(context, StepRecord.class);
        resetIntent.putExtra("user_id_pk", user_id_pk);
        resetIntent.putExtra("daily_step", currentStep);
        resetIntent.putExtra("daily_distance", daily_distance);

        // PendingIntent(보류 인텐트)를 사용하여 지정한 시간에 intent 실행
        // FLAG_CANCEL_CURRENT : 이전에 생성한 PendingIntent 는 취소하고 새롭게 만든다.
        // FLAG_NO_CREATE : 이미 생성된 PendingIntent 가 없다면 null 을 return 한다. 생성된 녀석이 있다면 그 PendingIntent 를 반환한다. 즉 재사용 전용이다.
        // FLAG_ONE_SHOT : 이 flag 로 생성한 PendingIntent 는 일회용이다.
        // FLAG_UPDATE_CURRENT : 이미 생성된 PendingIntent 가 존재하면 해당 Intent 의 Extra Data 만 변경한다.

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

        distance_sum.clear();
        daily_distance = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { // 가속도 센서

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) { // 지도 설정
        mMap = googleMap;
        setDefaultLocation(); // 퍼미션 체크 이전에 디폴트 위치로 표시 (현재는 서울 마지막 저장위치로 하도록 수정할 예정)
        OnCheckPermission(); // 퍼미션 체크 및 현재 위치 받기 시작

        // 현재위치로 돌아가는 버튼
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnMapClickListener(latLng -> Log.d("onMapClick", "onMapClick :"));
    }

    // 위치 퍼미션 확인, 현재위치 받아오기 시작 startLocationUpdate();
    public void OnCheckPermission() {
        int fineLocationCheck = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        int coarseLocationCheck = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        // 위치 퍼미션 있는지 체크
        if (fineLocationCheck == PackageManager.PERMISSION_GRANTED && coarseLocationCheck == PackageManager.PERMISSION_GRANTED) {
            // 1. 이미 위치 퍼미션 허가 되있다면 현재위치 받아오기 시작
            startLocationUpdate();
        } else {
            // 2. 위치 퍼미션 허가 안해놨다면 권한 요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 2.1 퍼미션 허가를 거부 한 적이 있는 경우 확인 메세지 띄우고 권한 요청
                Snackbar.make(main_layout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MainPage.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 2.2 퍼미션 허가를 거부 한 적이 없는 경우 바로 권한 요청
                ActivityCompat.requestPermissions(MainPage.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);
                if (startLatLng.latitude == 0 && startLatLng.longitude == 0) {
                    startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(startLatLng);
                    mMap.moveCamera(cameraUpdate);
                }
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + location.getLatitude() + " 경도:" + location.getLongitude();

                Log.d("onLocationResult", "현재위치 : " + markerSnippet);

                //현재 위치에 마커 생성, 폴리라인 생성후 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                currentLocation = location;
                endLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(7).color(Color.BLACK).geodesic(true);
                polyLine.add(mMap.addPolyline(options));

                double distance = SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
                Log.d("onLocationResult", "이동거리 : " + distance + "m");

                if (distance != 0)
                    distance_sum.add(distance);

                startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
    };

    public void startLocationUpdate() {
        if (!checkLocationServicesStatus()) {
            Log.d("startLocationUpdate", "call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            int fineLocationCheck = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int coarseLocationCheck = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (fineLocationCheck != PackageManager.PERMISSION_GRANTED ||
                    coarseLocationCheck != PackageManager.PERMISSION_GRANTED) {
                Log.d("startLocationUpdate", "퍼미션 안가지고 있음");
                return;
            }
            Log.d("startLocationUpdate", "call fusedLocationClient.requestLocationUpdates");
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }

    private boolean checkPermission() {
        int fineLocationCheck = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationCheck = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return fineLocationCheck == PackageManager.PERMISSION_GRANTED &&
                coarseLocationCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == 3) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdate();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다. 2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mDrawerLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();

                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mDrawerLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    // GPS 사용 체크
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // GPS 활성화 체크
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainPage.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // GPS 설정화면 띄우기
                Intent GPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(GPSSettingIntent);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    //지오코더: GPS를 주소로 변환
    public String getCurrentAddress(LatLng latlng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";

        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }

    }

    //디폴트 위치, 서울로 해놨음
    public void setDefaultLocation() {
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }

    // 현재위치에 마커 생성
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);
    }

    // 1. 위치퍼미션 체크 및 허가 (완료)
    // 2. 현재위치 받기(완료)
    // 3. 마커 설정 및 클릭등 이벤트 설정(미구상)
    // 4. 이동경로 그리기 및 거리측정 (경로는 위도,경도 바뀔때마다 폴리라인 그리면 될듯?)
    // 총 이동거리 = 이동경로길이(그려진 폴리라인의 총 길이) or 걸음수와 보폭 둘다 실제로 테스트해보고 더 정확한것으로 결정
    // 이동거리 측정은 완벽하게 하는것은 현재 불가능하다고 판단(핸드폰 센서 정밀도등 이슈로 대기업도 정확한 측정 불가능.)
    // 삼성헬스 자동거리측정 방법인 걸음수와 보폭(키와 분당 걸음 수로 구한 값)으로 이동거리 계산.
    // 삼성헬스 정밀거리측정 있으나 구글링 결과 계산이 너무 전문적이고 마찬가지로 완벽하게 정확하진 않다고 한다.
}