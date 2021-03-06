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
import com.app.hamzaworld.activity.GrabOfferActivity;
import com.app.hamzaworld.activity.GrabProductActivity;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import java.util.ArrayList;
import java.util.HashMap;

public class GrabProductAdapter extends RecyclerView.Adapter<GrabProductAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> productList;
    ImageLoader imageLoader;

    public GrabProductAdapter(Context context, ArrayList<HashMap<String, String>> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grab_product_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String,String> map = productList.get(i);

        myViewHolder.name.setText(map.get("product"));
        myViewHolder.price.setText("₹"+map.get("price"));
        myViewHolder.cprice.setText("₹"+map.get("cross_price"));
        myViewHolder.cprice.setPaintFlags(myViewHolder.cprice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + map.get("image"), ImageLoader.getImageListener(myViewHolder.image, R.drawable.image_preview, R.drawable.image_alert));
        myViewHolder.image.setImageUrl(Helper.IMAGE_URL + map.get("image"), imageLoader);
        myViewHolder.image.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, GrabProductActivity.class);
                intent.putExtra("id", map.get("id"));
                intent.putExtra("product", map.get("product"));
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView price, cprice, name;
        NetworkImageView image;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            cprice = itemView.findViewById(R.id.product_cprice);
            cardView = itemView.findViewById(R.id.cv_product);
            image = itemView.findViewById(R.id.product_image);
        }
    }
}
