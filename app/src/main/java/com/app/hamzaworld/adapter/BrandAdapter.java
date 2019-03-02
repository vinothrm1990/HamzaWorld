package com.app.hamzaworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.hamzaworld.R;
import com.app.hamzaworld.activity.ProductActivity;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import java.util.ArrayList;
import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> brandList;
    ImageLoader imageLoader;

    public BrandAdapter(Context context, ArrayList<HashMap<String, String>> brandList) {
        this.context = context;
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.brand_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String, String> map = brandList.get(i);

        myViewHolder.name.setText(map.get("brand"));

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + map.get("proimage"), ImageLoader.getImageListener(myViewHolder.image, R.drawable.image_preview, R.drawable.image_alert));
        myViewHolder.image.setImageUrl(Helper.IMAGE_URL + map.get("proimage"), imageLoader);
        myViewHolder.image.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("cat", map.get("cat"));
                intent.putExtra("subcat", map.get("subcat"));
                intent.putExtra("brand", map.get("brand"));
                context.startActivity(intent);
                Bungee.swipeRight(context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView image;
        TextView name;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.brand_subcat_iv);
            name = itemView.findViewById(R.id.brand_subcat_tv);
            cardView = itemView.findViewById(R.id.cv_brand);

        }

    }
}
