package com.app.hamzaworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.hamzaworld.R;
import com.app.hamzaworld.activity.BagDetailActivity;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.app.hamzaworld.activity.BagActivity.bagList;
import static com.app.hamzaworld.activity.BagActivity.progress;

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String,String>> bagList;
    ImageLoader imageLoader;
    String REMOVE_BAG_URL = Helper.BASE_URL + Helper.ADD_REMOVE_WISHLIST;

    public BagAdapter(Context context, ArrayList<HashMap<String, String>> bagList) {
        this.context = context;
        this.bagList = bagList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bag_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        final HashMap<String, String> map = bagList.get(i);

        myViewHolder.tvPName.setText(map.get("category")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("cross_price"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        if (map.get("prate").isEmpty()){
            myViewHolder.tvPrate.setText("0");
        }else {
            myViewHolder.tvPrate.setText(map.get("prate"));
        }
        if (map.get("trate").isEmpty()){
            myViewHolder.tvTRate.setText("(0)");
        }else {
            myViewHolder.tvTRate.setText("("+map.get("trate")+")");
        }
        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.image_preview, R.drawable.image_alert));
        myViewHolder.ivImage.setImageUrl(Helper.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        myViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cusid = Prefs.getString("mobile", "");
                String proid = map.get("id");
                int flag = 0;
                int pos = myViewHolder.getAdapterPosition();
                removeBag(pos, cusid, proid, flag);

            }
        });

        myViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String proid = map.get("id");
                String proname = map.get("product");

                Intent intent = new Intent(context, BagDetailActivity.class);
                intent.putExtra("id", proid);
                intent.putExtra("product", proname);
                context.startActivity(intent);
                //getWishDetails(cusid, proid);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bagList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice, tvPrate, tvTRate;
        NetworkImageView ivImage;
        Button btnDetail, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPName = itemView.findViewById(R.id.bag_pro_name);
            tvPPrice = itemView.findViewById(R.id.bag_price);
            tvPCPrice = itemView.findViewById(R.id.bag_cross_price);
            ivImage = itemView.findViewById(R.id.bag_image);
            btnDetail = itemView.findViewById(R.id.bag_btn_detail);
            btnRemove = itemView.findViewById(R.id.bag_btn_remove);
            tvPrate = itemView.findViewById(R.id.bag_rate);
            tvTRate = itemView.findViewById(R.id.bag_total_rate);
        }
    }

    private void removeBag(final int pos, final String cusid, final String proid, final int flag) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, REMOVE_BAG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){

                                    progress.setVisibility(View.GONE);

                                    bagList.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, bagList.size());
                                    bagList();

                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                FBToast.errorToast(context, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            FBToast.errorToast(context, e.getMessage(), FBToast.LENGTH_SHORT);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progress.setVisibility(View.GONE);
                        FBToast.errorToast(context, error.getMessage(), FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                params.put("product_id", proid);
                params.put("flag", String.valueOf(flag));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }
}
