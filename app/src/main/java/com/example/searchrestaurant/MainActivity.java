package com.example.searchrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // activity_main.xml (タイトル画面) を表示
        setContentView(R.layout.activity_main);

        // Handlerを使って遅延処理を実行
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(MainActivity.this, SearchInputView.class);

                // 2. 画面を起動
                startActivity(mainIntent);

                // 3. 起動元の画面（MainActivity）を終了
                finish();
            }
        }, SPLASH_DELAY_TIME);
    }
}