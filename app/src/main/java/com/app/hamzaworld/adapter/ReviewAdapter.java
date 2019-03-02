package com.app.hamzaworld.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.app.hamzaworld.R;
import java.util.ArrayList;
import java.util.HashMap;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String, String>> reviewList;

    public ReviewAdapter(Context context, ArrayList<HashMap<String, String>> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        HashMap<String, String> map = reviewList.get(i);

        myViewHolder.tvName.setText(map.get("name"));
        myViewHolder.tvTime.setText(map.get("date"));
        myViewHolder.jtvReview.setText(map.get("review"));
        myViewHolder.ratingBar.setRating(Float.parseFloat(map.get("rating")));
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialRatingBar ratingBar;
        TextView tvName, tvTime;
        TextView jtvReview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
            tvName = itemView.findViewById(R.id.review_name);
            tvTime = itemView.findViewById(R.id.review_timestamp);
            jtvReview = itemView.findViewById(R.id.review_review);
        }
    }
}
