package com.app.hamzaworld.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import com.app.hamzaworld.other.OnDataChangeListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.app.hamzaworld.activity.CartActivity.cartList;
import static com.app.hamzaworld.activity.CartActivity.grandTotal;
import static com.app.hamzaworld.activity.CartActivity.progress;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    Context context;
    ArrayList<HashMap<String,String>> cartList;
    ImageLoader imageLoader;
    HashMap<String, String> dataMap;
    OnDataChangeListener mOnDataChangeListener;
    String ADD_QTY_URL = Helper.BASE_URL + Helper.ADD_QUANTITY;
    String ADD_REMOVE_URL = Helper.BASE_URL + Helper.ADD_REMOVE_CART;

    public CartAdapter(Context context, ArrayList<HashMap<String, String>> cartList) {
        this.context = context;
        this.cartList = cartList;
    }


    public void setOnDataChangeListener(Context mcontext,OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
        context=mcontext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_adapter, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final HashMap<String,String> map = cartList.get(i);

        myViewHolder.tvPName.setText(map.get("brand")+ "\t" +map.get("product"));
        myViewHolder.tvPPrice.setText( "₹"+map.get("price"));
        myViewHolder.tvPCPrice.setText( "₹"+map.get("cross_price"));
        myViewHolder.tvPCPrice.setPaintFlags(myViewHolder.tvPCPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        myViewHolder.tvColor.setText(map.get("color"));
        myViewHolder.tvSize.setText(map.get("size"));

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + map.get("pro_image"), ImageLoader.getImageListener(myViewHolder.ivImage, R.drawable.image_preview, R.drawable.image_alert));
        myViewHolder.ivImage.setImageUrl(Helper.IMAGE_URL + map.get("pro_image"), imageLoader);
        myViewHolder.ivImage.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        String quantity= map.get("quantity");
        if (quantity != null){
            final String qty[] = new String[]{

                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
            };

            List<String> stringList = new ArrayList<>(Arrays.asList(qty));
            ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myViewHolder.spQty.setAdapter(arrayAdapter);
            myViewHolder.spQty.setSelection(arrayAdapter.getPosition(quantity));
        }else {
            final String qty[] = new String[]{

                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
            };

            List<String> stringList = new ArrayList<>(Arrays.asList(qty));
            ArrayAdapter<String > arrayAdapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stringList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myViewHolder.spQty.setAdapter(arrayAdapter);
        }

        myViewHolder.spQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = parent.getItemAtPosition(position).toString();
                String price = map.get("price");
                String totalprice = String.valueOf(Integer.parseInt(selectedItem) * Float.parseFloat(price));
                myViewHolder.tvTotalPrice.setText("₹"+totalprice);

                dataMap = new HashMap<String, String>();
                map.put("qty", selectedItem);
                map.put("totalprice", totalprice);
                dataMap = map;
                cartList.get(i).putAll(dataMap);

                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onDataChanged(grandTotal());
                    String cusid = Prefs.getString("mobile", "");
                    String proid = map.get("id");
                    addQuantity(cusid, proid, selectedItem, totalprice);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        myViewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cusid = Prefs.getString("mobile", "");
                String proid = map.get("id");
                int flag = 0;
                int pos = myViewHolder.getAdapterPosition();
                removeCart(pos, cusid, proid, flag);

            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPName, tvPPrice, tvPCPrice, tvTotalPrice, tvColor, tvSize;
        Spinner spQty;
        NetworkImageView ivImage;
        Button btnSave, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvColor = itemView.findViewById(R.id.cart_tv_color);
            tvSize = itemView.findViewById(R.id.cart_tv_size);
            tvPName = itemView.findViewById(R.id.cart_pro_name);
            tvPPrice = itemView.findViewById(R.id.cart_price);
            tvPCPrice = itemView.findViewById(R.id.cart_cross_price);
            tvTotalPrice = itemView.findViewById(R.id.cart_total_price);
            spQty = itemView.findViewById(R.id.cart_qty);
            ivImage = itemView.findViewById(R.id.cart_image);
            btnSave = itemView.findViewById(R.id.cart_btn_save);
            btnRemove = itemView.findViewById(R.id.cart_btn_remove);
        }
    }

    private void addQuantity(final String cusid, final String proid, final String selectedItem, final String totalprice) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, ADD_QTY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    progress.setVisibility(View.GONE);

                                    Prefs.putString("b_qty", selectedItem);
                                    Prefs.putString("b_total", totalprice);
                                    //validUtils.showToast(context, jsonObject.getString("data"));
                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    FBToast.errorToast(context, jsonObject.getString("data"), FBToast.LENGTH_SHORT);
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
                params.put("quantity", selectedItem);
                params.put("total", totalprice);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    private void removeCart(final int pos, final String cusid, final String proid, final int flag) {

        progress.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, ADD_REMOVE_URL,
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
                                    mOnDataChangeListener.onDataChanged(grandTotal());
                                    cartList.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, cartList.size());
                                    cartList();
                                    FBToast.successToast(context, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
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
