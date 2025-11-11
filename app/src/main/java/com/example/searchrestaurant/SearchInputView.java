package com.example.searchrestaurant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.os.Looper;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;



public class SearchInputView extends AppCompatActivity {

    private static final String API_KEY = "26abb7e7a8cb9ea5";
    private static final String BASE_URL = "http://webservice.recruit.co.jp/";

    private FusedLocationProviderClient fusedLocationClient;
    private ApiService apiService;
    private Gson gson = new Gson();

    private Spinner radiusSpinner;
    private Button searchButton;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    /*private static final String PARAM_LUNCH = "lunch";
    private static final String PARAM_ROOM = "private_room";
    private static final String PARAM_SMOKE = "non_smoking";
    private static final String PARAM_GENRE = "genre";
    private static final String VALUE_TRUE = "1";
    private static final String VALUE_IZAKAYA = "G001";*/

    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_input_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener(v -> {
            requestLocationPermission();
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        radiusSpinner = findViewById(R.id.spinner_radius);

    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 権限が許可されていない場合、ユーザーに許可を求める
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 権限が既に許可されている場合、位置情報取得に進む
            //getLastLocation();
            requestLocationUpdatesAndSearch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 権限が許可された
                //getLastLocation();
                requestLocationUpdatesAndSearch();
            } else {
                // 権限が拒否された
                Toast.makeText(this, "位置情報権限がないため、現在地検索はできません。", Toast.LENGTH_LONG).show();
            }
        }
    }

    private LocationRequest createLocationRequest() {
        // API 31 (Android 12) 以降の推奨される方法
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(2000)
                .build();
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdatesAndSearch() {
        LocationRequest locationRequest = createLocationRequest();

        // 位置情報が取得されたときに実行されるコールバック
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    return;
                }

                // 取得された位置情報のリストから最新のものを取得
                android.location.Location location = locationResult.getLastLocation();

                if (location != null) {
                    // 1. 位置情報が取得できたら、すぐに更新を停止
                    fusedLocationClient.removeLocationUpdates(locationCallback);

                    // 2. 検索処理へ進む
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    int selectedIndex = radiusSpinner.getSelectedItemPosition();
                    int searchRange = selectedIndex + 1;

                    Log.d("GPS_STATUS", "位置情報を更新で取得: " + latitude + ", " + longitude);
                    searchRestaurants(latitude, longitude, searchRange);

                }
            }
        };

        // 位置情報の取得を開始
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        Toast.makeText(this, "現在地を探索中です...", Toast.LENGTH_SHORT).show();
    }

    /*private Map<String, String> getSelectedFilterParams() {
        Map<String, String> params = new HashMap<>();

        // 4つの静的ボタンの状態をチェックし、Mapにパラメータを追加
        // XMLで定義したIDを使ってButtonオブジェクトを取得
        if (isButtonSelected(findViewById(R.id.btn_lunch))) {
            params.put(PARAM_LUNCH, VALUE_TRUE);
        }
        if (isButtonSelected(findViewById(R.id.btn_private_room))) {
            params.put(PARAM_ROOM, VALUE_TRUE);
        }
        if (isButtonSelected(findViewById(R.id.btn_non_smoking))) {
            params.put(PARAM_SMOKE, VALUE_TRUE);
        }
        if (isButtonSelected(findViewById(R.id.btn_izakaya))) {
            params.put(PARAM_GENRE, VALUE_IZAKAYA);
        }
        Button btnLunch = findViewById(R.id.btn_lunch);
        if (isButtonSelected(btnLunch)) {
            params.put(PARAM_LUNCH, VALUE_TRUE);
        }
        return params;
    }*/

    private void searchRestaurants(double lat, double lng, int range/*, Map<String, String> filterParams*/) {
        // API呼び出しの準備
        Call<GourmetResponse> call = apiService.searchShop(
                API_KEY,
                lat,
                lng,
                range,
                1, // 最初のページ (start=1)
                "json"
                /*filterParams.getOrDefault("lunch", null),
                filterParams.getOrDefault("private_room", null),
                filterParams.getOrDefault("non_smoking", null),
                filterParams.getOrDefault("genre", null)*/
        );
        // 非同期でリクエストを実行
        call.enqueue(new Callback<GourmetResponse>() {
            @Override
            public void onResponse(Call<GourmetResponse> call, Response<GourmetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GourmetResponse gourmetResponse = response.body();
                    List<Shop> shopList = gourmetResponse.getResults().getShop();

                    if (shopList != null && !shopList.isEmpty()) {
                        // 検索結果のリストをJSON文字列に変換
                        String shopsJson = gson.toJson(shopList);

                        // 検索結果画面へ遷移し、データを渡す
                        Intent intent = new Intent(SearchInputView.this, SearchResultView.class);
                        intent.putExtra("SHOP_LIST_JSON", shopsJson);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SearchInputView.this, "検索結果が見つかりませんでした。", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(SearchInputView.this, "API通信エラー: " + response.code(), Toast.LENGTH_LONG).show();
                    Log.e("API", "API Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GourmetResponse> call, Throwable t) {
                Log.e("API", "通信失敗", t);
                Toast.makeText(SearchInputView.this, "通信に失敗しました。", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*private void setButtonState(Button button, boolean isSelected) {
        if (isSelected) {
            // 選択状態: 水色背景、白文字
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.search_button_blue));
            button.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            // 未選択状態: 白背景、水色文字
            button.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.white));
            button.setTextColor(ContextCompat.getColor(this, R.color.search_button_blue));
        }
    }*/
}