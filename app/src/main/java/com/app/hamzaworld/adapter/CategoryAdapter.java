package com.app.hamzaworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.hamzaworld.R;
import com.app.hamzaworld.activity.CategoryActivity;
import com.app.hamzaworld.data.Category;
import com.bumptech.glide.Glide;
import java.util.List;

import spencerstudios.com.bungeelib.Bungee;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Context context;
    List<Category> catList;

    public CategoryAdapter(Context context, List<Category> catList) {
        this.context = context;
        this.catList = catList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final Category menu = catList.get(i);

        myViewHolder.tvName.setText(menu.getName());
        Glide.with(context).load(menu.getIcon()).into(myViewHolder.ivIcon);

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i == 0){
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category", "Clothings");
                    context.startActivity(intent);
                    Bungee.swipeRight(context);
                }else  if (i == 1){
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category", "Footwear");
                    context.startActivity(intent);
                    Bungee.swipeRight(context);
                }else if (i == 2){
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category", "Beauty");
                    context.startActivity(intent);
                    Bungee.swipeRight(context);
                }else if (i == 3){
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("category", "Clothings");
                    context.startActivity(intent);
                    Bungee.swipeRight(context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivIcon;
        LinearLayout linearLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.cat_home_tv);
            ivIcon = itemView.findViewById(R.id.cat_home_iv);
            linearLayout = itemView.findViewById(R.id.category_layout);
        }
    }
}
