package com.example.searchrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.example.searchrestaurant.Shop;

public class SearchResultView  extends AppCompatActivity implements ShopAdapter.OnItemClickListenerS {

    private RecyclerView recyclerView;
    private List<Shop> shops;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // アクションバーにタイトルを設定
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("レストラン検索結果");
        }

        recyclerView = findViewById(R.id.recycler_view_shops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1. IntentからJSON文字列を受け取る
        String shopListJson = getIntent().getStringExtra("SHOP_LIST_JSON");

        if (shopListJson != null) {
            // 2. JSON文字列をList<Shop>オブジェクトに変換
            Type listType = new TypeToken<ArrayList<Shop>>() {}.getType();
            List<Shop> shopList = gson.fromJson(shopListJson, listType);

            if (shopList != null && !shopList.isEmpty()) {
                // 3. RecyclerViewにデータをセット
                adapter = new ShopAdapter(shopList, this); // this でクリックリスナーを渡す
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "検索結果が空です。", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "店舗データを受け取れませんでした。", Toast.LENGTH_LONG).show();
            finish(); // データがなければ画面を閉じる
        }
    }

    // 4. 要件: 検索結果画面で店舗がクリックされたときの処理 (店舗詳細画面へ遷移)
    @Override
    public void onItemClick(Shop shop) {
        // 店舗詳細画面（ShopDetailActivity）へ遷移

        // TODO: ShopDetailActivityを先に作成する
        Intent detailIntent = new Intent(SearchResultActivity.this, ShopDetailActivity.class);

        // 選択された店舗情報をJSONに戻して渡す
        String shopJson = gson.toJson(shop);
        detailIntent.putExtra("SELECTED_SHOP_JSON", shopJson);

        startActivity(detailIntent);
    }
}
