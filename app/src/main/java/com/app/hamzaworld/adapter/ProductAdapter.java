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
import com.app.hamzaworld.activity.DetailActivity;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import java.util.ArrayList;
import java.util.HashMap;
import spencerstudios.com.bungeelib.Bungee;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> productList;
    ImageLoader imageLoader;

    public ProductAdapter(Context context, ArrayList<HashMap<String, String>> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String, String> map = productList.get(i);

        myViewHolder.name.setText(map.get("product"));
        myViewHolder.price.setText("₹"+map.get("price"));
        myViewHolder.cprice.setText("₹"+map.get("crossprice"));
        myViewHolder.cprice.setPaintFlags(myViewHolder.cprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        if (!map.get("prate").isEmpty()){
            myViewHolder.prate.setText(map.get("prate"));
        }else {
            myViewHolder.prate.setText("0");
        }
        if (!map.get("trate").isEmpty()){
            myViewHolder.trate.setText(map.get("trate"));
        }else {
            myViewHolder.trate.setText("(0)");
        }

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + map.get("proimage"), ImageLoader.getImageListener(myViewHolder.image, R.drawable.image_preview, R.drawable.image_alert));
        myViewHolder.image.setImageUrl(Helper.IMAGE_URL + map.get("proimage"), imageLoader);
        myViewHolder.image.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", map.get("id"));
                intent.putExtra("product", map.get("product"));
                context.startActivity(intent);
                Bungee.swipeRight(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView image;
        TextView name, price, cprice, prate, trate;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.product_iv);
            name = itemView.findViewById(R.id.product_tv);
            cardView = itemView.findViewById(R.id.cv_product);
            price = itemView.findViewById(R.id.product_price);
            cprice = itemView.findViewById(R.id.product_cprice);
            prate = itemView.findViewById(R.id.product_prate);
            trate = itemView.findViewById(R.id.product_trate);
        }
    }
}
