package com.example.searchrestaurant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // APIのベースURL以下のパスとメソッドを定義
    @GET("hotpepper/gourmet/v1/")
    Call<GourmetResponse> searchShop(
            // APIキー
            @Query("key") String apiKey,
            // 検索する緯度
            @Query("lat") double latitude,
            // 検索する経度
            @Query("lng") double longitude,
            // 検索半径 (1:300m, 2:500m, 3:1000m, 4:2000m, 5:3000m)
            @Query("range") int range,
            // 開始位置（ページング用）
            @Query("start") int start,
            // フォーマットはJSONを指定
            @Query("format") String format

            //こだわり条件
            /*@Query("lunch") String lunchFlag,         // "1" or null
            @Query("private_room") String roomFlag,   // "1" or null
            @Query("non_smoking") String smokeFlag,   // "1" or null
            @Query("genre") String genreCode         // "G001" or null*/
    );
}
