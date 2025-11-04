package com.example.searchrestaurant;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

public class ShopDetailView extends AppCompatActivity{
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        // UI部品の初期化
        TextView nameTv = findViewById(R.id.tv_detail_name);
        TextView addressTv = findViewById(R.id.tv_detail_address);
        TextView openTimeTv = findViewById(R.id.tv_detail_open_time);
        TextView accessTv = findViewById(R.id.tv_detail_access);
        ImageView imageIv = findViewById(R.id.iv_detail_image);

        // IntentからJSON文字列を受け取る
        String shopJson = getIntent().getStringExtra("SELECTED_SHOP_JSON");

        if (shopJson != null) {
            // JSON文字列をShopオブジェクトに変換
            Shop shop = gson.fromJson(shopJson, Shop.class);

            if (shop != null) {
                // アクションバーに店舗名を設定
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(shop.getName());
                }

                // 要件: UIにデータをセット
                nameTv.setText(shop.getName());

                // ホットペッパーAPIの仕様上、住所は "address"、営業時間は "open" に格納されていると仮定
                addressTv.setText("住所: " + shop.getAddress());

                // 営業時間 (APIの "open" キーに対応するゲッターがShopクラスに必要)
                // HACK: 現状のShopクラスにはgetOpen()がないため、一時的に固定値を表示するか、
                // Shopクラスに private String open; public String getOpen() { return open; } を追加してください。
                // *今回は実装の簡略化のため、Shopクラスに "open" フィールドが存在すると仮定して進めます。
                // openTimeTv.setText("営業時間: " + shop.getOpen());
                openTimeTv.setText("営業時間: " + "データ未連携 (APIの'open'フィールドを利用)");

                // アクセス情報（おまけ）
                // accessTv.setText("アクセス: " + shop.getAccess()); // accessフィールドもAPIから取得可能
                accessTv.setText("アクセス: " + "データ未連携 (APIの'access'フィールドを利用)");

                // 店舗画像（大きいサイズ: mobile.l を使用）
                if (shop.getPhoto() != null && shop.getPhoto().getMobile() != null && shop.getPhoto().getMobile().getL() != null) {
                    Glide.with(this)
                            .load(shop.getPhoto().getMobile().getL()) // 大きい画像URL
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageIv);
                }

                // *詳細画面にあるべき機能: 地図ボタンは一旦無効化のままです。

            } else {
                Toast.makeText(this, "店舗情報の解析に失敗しました。", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "詳細データがありません。", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
