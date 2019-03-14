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
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.hamzaworld.R;
import com.app.hamzaworld.activity.OrderDetailActivity;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String,String>> orderList;
    ImageLoader imageLoader;

    public OrderAdapter(Context context, ArrayList<HashMap<String, String>> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        final HashMap<String,String> map = orderList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("crossprice"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        myViewHolder.tvTotalPrice.setText("₹"+map.get("total"));
        myViewHolder.tvQuantity.setText(map.get("qty"));
        myViewHolder.tvColor.setText(map.get("color"));
        myViewHolder.tvSize.setText(map.get("size"));

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + map.get("proimage"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.image_preview, R.drawable.image_alert));
        myViewHolder.ivImage.setImageUrl(Helper.IMAGE_URL + map.get("proimage"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        myViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String color = map.get("color");
                String size = map.get("size");

                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("id", map.get("id"));
                intent.putExtra("product", map.get("product"));
                intent.putExtra("color", color);
                intent.putExtra("size", size);

                context.startActivity(intent);
            }
        });

        /*myViewHolder.btnInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/


    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice, tvTotalPrice, tvQuantity, tvColor, tvSize;
        NetworkImageView ivImage;
        CardView cardView;
        Button btnDetail, btnInvoice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvColor = itemView.findViewById(R.id.order_tv_color);
            tvSize = itemView.findViewById(R.id.order_tv_size);
            tvPName = itemView.findViewById(R.id.order_pro_name);
            tvPPrice = itemView.findViewById(R.id.order_price);
            tvPCPrice = itemView.findViewById(R.id.order_cross_price);
            tvTotalPrice = itemView.findViewById(R.id.order_total_price);
            tvQuantity = itemView.findViewById(R.id.order_qty);
            ivImage = itemView.findViewById(R.id.order_image);
            cardView = itemView.findViewById(R.id.cv_order);
            btnDetail = itemView.findViewById(R.id.order_btn_detail);
            //btnInvoice = itemView.findViewById(R.id.order_btn_invoice);
        }
    }
}
