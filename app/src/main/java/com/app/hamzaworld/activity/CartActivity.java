package com.app.hamzaworld.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.hamzaworld.R;
import com.app.hamzaworld.adapter.CartAdapter;
import com.app.hamzaworld.adapter.SubCategoryAdapter;
import com.app.hamzaworld.other.HamzaWorld;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.OnDataChangeListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.ishankhanna.UberProgressView;
import spencerstudios.com.bungeelib.Bungee;

public class CartActivity extends AppCompatActivity implements InternetConnectivityListener, OnDataChangeListener {

    InternetAvailabilityChecker availabilityChecker;
    RecyclerView rvCart;
    Button btnCheckout, btnContinue;
    HashMap<String, String> map;
    TextView tvTotalAmount;
    CartAdapter cartAdapter;
    RecyclerView.LayoutManager layoutManager;
    public static UberProgressView progress;
    public static float total;
    String cus_id;
    public static LinearLayout cartLayout, bottomLayout, emptyLayout;
    public static ArrayList<HashMap<String,String>> cartList;
    String GET_CART_URL = Helper.BASE_URL + Helper.GET_CART;
    String CHECK_URL = Helper.BASE_URL + Helper.CHECK_ORDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("My Cart");
        title.setTextSize(23);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        progress = findViewById(R.id.cart_progress);
        rvCart = findViewById(R.id.rv_cart);
        tvTotalAmount = findViewById(R.id.cart_total_amount);
        btnCheckout = findViewById(R.id.cart_btn_proceed);
        btnContinue = findViewById(R.id.cart_btn_continue);
        cartLayout = findViewById(R.id.cart_layout);
        emptyLayout = findViewById(R.id.cart_no_layout);
        bottomLayout = findViewById(R.id.bottom_layout);

        cartList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        rvCart.setLayoutManager(layoutManager);

        cus_id = Prefs.getString("mobile", null);

        if (cus_id!=null && !cus_id.isEmpty()) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String timestamp = sdf.format(now);
            getCart(cus_id, timestamp);
        }else {
            cartLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            FBToast.infoToast(CartActivity.this, "Please Register or Login to View Your Cart", FBToast.LENGTH_LONG);
        }

        tvTotalAmount.setText("₹"+String.valueOf(grandTotal()));

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CartActivity.this, HomeActivity.class));
                Bungee.swipeRight(CartActivity.this);
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("total", String.valueOf(grandTotal()));
                startActivity(intent);
            }
        });

    }

    private void getCart(final String cus_id, final String timestamp) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, GET_CART_URL,
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
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    cartLayout.setVisibility(View.VISIBLE);
                                    bottomLayout.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);

                                    cartList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);

                                        map = new HashMap<String, String>();

                                        String id = object.getString("id");
                                        String brand = object.getString("sub_product");
                                        String product = object.getString("pname");
                                        String pro_image = object.getString("image");
                                        String price =  object.getString("price");
                                        String crossprice =  object.getString("cross_price");
                                        String branch_id =  object.getString("b_id");
                                        String branch_name =  object.getString("branchname");
                                        String branch_number =  object.getString("b_mobile");
                                        String qty =  object.getString("c_qty");
                                        String cartid =  object.getString("cart_id");
                                        String pur_date =  object.getString("purchase_date");

                                        map.put("id", id);
                                        map.put("brand", brand);
                                        map.put("product", product);
                                        map.put("pro_image", pro_image);
                                        map.put("price", price);
                                        map.put("cross_price", crossprice);
                                        map.put("branch_id", branch_id);
                                        map.put("branch_name", branch_name);
                                        map.put("branch_number", branch_number);
                                        map.put("quantity", qty);
                                        map.put("cartid", cartid);
                                        map.put("pdate", pur_date);

                                        cartList.add(map);

                                    }

                                    cartAdapter = new CartAdapter(CartActivity.this, cartList);
                                    rvCart.setAdapter(cartAdapter);
                                    cartAdapter.setOnDataChangeListener(CartActivity.this, CartActivity.this);
                                    cartAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    cartLayout.setVisibility(View.GONE);
                                    bottomLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    FBToast.warningToast(CartActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(CartActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(CartActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(CartActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        String message = null;

                        if (error instanceof NetworkError){
                            message = "Can't Connect to Network!";
                        }else if (error instanceof ServerError){
                            message = "Server could not be Found!";
                        }else if (error instanceof AuthFailureError){
                            message = "Can't Connect to Network!";
                        }else if (error instanceof ParseError){
                            message = "Parsing Error!";
                        }else if (error instanceof NoConnectionError){
                            message = "Can't connect to Network!";
                        }else if (error instanceof TimeoutError){
                            message = "Connection Timeout!";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        AlertDialog alertDialog = builder.create();
                        builder.setTitle("NETWORK ERROR")
                                .setMessage(message)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .setCancelable(false);
                        alertDialog.show();
                        FBToast.errorToast(CartActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("date", timestamp);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CartActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    public static float grandTotal(){
        total = 0;
        for(int i = 0 ; i < cartList.size(); i++) {
            if (cartList.get(i).get("totalprice") != null) {
                total += Float.parseFloat(cartList.get(i).get("totalprice"));
            }
        }
        return total;
    }

    public static void cartList(){
        if (cartList.size()==0){
            cartLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
        }else {
            cartLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CartActivity.this, HomeActivity.class));
        Bungee.swipeLeft(CartActivity.this);
    }*/

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        HamzaWorld.freeMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        availabilityChecker.addInternetConnectivityListener(this);

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if (!is3g && !isWifi)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NETWORK ERROR")
                    .setMessage("Check your Internet Connection")
                    .setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                            FBToast.warningToast(getApplicationContext(),"Please make sure your Network Connection is ON ",FBToast.LENGTH_SHORT);
                        }
                    })
                    .setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if (!isConnected){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NETWORK ERROR")
                    .setMessage("Check your Internet Connection")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        availabilityChecker.removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onDataChanged(float total) {
        tvTotalAmount.setText("₹"+String.valueOf(total));
    }
}
