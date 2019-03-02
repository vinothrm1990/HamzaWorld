package com.app.hamzaworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.app.hamzaworld.R;
import com.app.hamzaworld.activity.BrandActivity;

import java.util.ArrayList;
import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> subCatList;

    public SubCategoryAdapter(Context context, ArrayList<HashMap<String, String>> subCatList) {
        this.context = context;
        this.subCatList = subCatList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.sub_category_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final HashMap<String, String> map = subCatList.get(i);

        myViewHolder.tvName.setText(map.get("subcat"));

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, BrandActivity.class);
                intent.putExtra("cat", map.get("cat"));
                intent.putExtra("subcat", map.get("subcat"));
                context.startActivity(intent);
                Bungee.swipeRight(context);
                //FBToast.infoToast(context, map.get("subcat"), FBToast.LENGTH_SHORT);
            }
        });


    }

    @Override
    public int getItemCount() {
        return subCatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.subcat_tv_name);
            cardView = itemView.findViewById(R.id.cv_subCat);
        }
    }
}
