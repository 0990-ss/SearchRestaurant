package com.example.searchrestaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder>{

    private List<Shop> shopList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Shop shop);
    }

    public ShopAdapter(List<Shop> shopList, OnItemClickListener listener) {
        this.shopList = shopList;
        this.listener = listener;
    }

    // 各項目（item_shop.xml）のViewを参照するためのクラス
    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView name;
        public TextView access;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_shop.xmlのUI部品をIDで取得
            thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            name = itemView.findViewById(R.id.tv_shop_name);
            access = itemView.findViewById(R.id.tv_distance);
        }
    }

    // 2. ViewHolderの作成 (item_shop.xmlをViewに変換)
    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ShopViewHolder(view);
    }

    // 3. データの紐付け（表示内容のセット）
    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shopList.get(position);

        // 要件: 店舗名称 / アクセス / サムネイル画像 をセット
        holder.name.setText(shop.getName());
        holder.access.setText(shop.getAccess());

        // Glideを使って画像URLからサムネイルを読み込み
        // モバイルサムネイル (mobile.s) を使用
        if (shop.getPhoto() != null && shop.getPhoto().getMobile() != null && shop.getPhoto().getMobile().getS() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(shop.getPhoto().getMobile().getS()) // サムネイルURL
                    .placeholder(R.drawable.ic_launcher_background) // 画像がない場合のプレースホルダ
                    .into(holder.thumbnail);
        } else {
            holder.thumbnail.setImageResource(R.drawable.ic_launcher_background); // 画像がない場合はデフォルト画像を表示
        }

        // 要件: 店舗詳細画面への遷移（クリック処理）
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(shop);
            }
        });
    }

    // 4. リストのサイズを返す
    @Override
    public int getItemCount() {
        return shopList.size();
    }
}