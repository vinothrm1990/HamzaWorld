package com.app.hamzaworld.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.app.hamzaworld.adapter.DetailColorAdapter;
import com.app.hamzaworld.adapter.DetailSizeAdapter;
import com.app.hamzaworld.adapter.GrabProductColorAdapter;
import com.app.hamzaworld.adapter.DetailAdapter;
import com.app.hamzaworld.adapter.ReviewAdapter;
import com.app.hamzaworld.adapter.GrabProductSizeAdapter;
import com.app.hamzaworld.data.AllColor;
import com.app.hamzaworld.data.AllSize;
import com.app.hamzaworld.other.HamzaWorld;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.OnColorChangeListener;
import com.app.hamzaworld.other.OnSizeChangeListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import in.ishankhanna.UberProgressView;
import spencerstudios.com.bungeelib.Bungee;

public class DetailActivity extends AppCompatActivity implements InternetConnectivityListener,
        OnColorChangeListener, OnSizeChangeListener {

    InternetAvailabilityChecker availabilityChecker;
    UberProgressView progress;
    Menu menu;
    String id, product;
    Button btnSave, btnCart;
    public static LinearLayout btnLayout;
    ArrayList<AllColor> colorList;
    ArrayList<AllSize> sizeList;
    ArrayList<HashMap<String, String>> reviewList;
    HashMap<String, String> map;
    LinearLayout tabProductLayout, tabDetailLayout, tabReviewLayout, reviewLayout,
    emptyReviewLayout;
    DetailColorAdapter colorAdapter;
    DetailSizeAdapter sizeAdapter;
    CirclePageIndicator pageIndicator;
    DetailAdapter detailAdapter;
    ViewPager viewPager;
    private static int NUM_PAGES = 0;
    private static int currentPage = 0;
    RecyclerView rvReview, rvColor, rvSize;
    ReviewAdapter reviewAdapter;
    RecyclerView.LayoutManager layoutManager;
    public static ScrollView detailLayout;
    LinearLayout emptyLayout, colorLayout, sizeLayout;
    String cus_id, b_id, b_name, b_mobile;
    TextView tvName, tvPrice, tvCrossPrice, tvRate, tvTabProduct, tvTabDetail, tvTabReview,
            tvProductColor, tvProductSize, tvStock, tvDetailCategory, tvDetailBrand, tvDetailDesc;
    String DETAIL_URL = Helper.BASE_URL + Helper.GET_DETAIL;
    String REVIEW_URL = Helper.BASE_URL + Helper.GET_RATING;
    String CART_URL = Helper.BASE_URL + Helper.ADD_REMOVE_CART;
    String GET_CART_FLAG_URL = Helper.BASE_URL + Helper.GET_CART_FLAG;
    String GET_BAG_FLAG_URL = Helper.BASE_URL + Helper.GET_WISH_FLAG;
    String BAG_URL = Helper.BASE_URL + Helper.ADD_REMOVE_WISHLIST;
    String SIZE_URL = Helper.BASE_URL + Helper.GET_SIZE;
    String GET_COLOR_SIZE_URL = Helper.BASE_URL + Helper.GET_COLOR_SIZE_PRODUCT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        product = intent.getStringExtra("product");

        if (id == null && product == null){
            id = Prefs.getString("did", null);
            product = Prefs.getString("dproduct", null);
        }else {
            Prefs.putString("did", id);
            Prefs.putString("dproduct", product);
        }

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText(product);
        title.setTextSize(23);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);
        title.setSingleLine(true);
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        colorLayout = findViewById(R.id.color_layout);
        sizeLayout = findViewById(R.id.size_layout);
        rvColor = findViewById(R.id.rv_radio_color);
        rvSize = findViewById(R.id.rv_radio_size);
        progress = findViewById(R.id.detail_progress);
        viewPager = findViewById(R.id.slide_pager);
        pageIndicator = findViewById(R.id.slide_indicator);
        detailLayout = findViewById(R.id.detail_layout);
        emptyLayout = findViewById(R.id.detail_empty_layout);
        tvName = findViewById(R.id.detail_tv_name);
        tvPrice = findViewById(R.id.detail_price);
        tvCrossPrice = findViewById(R.id.detail_cprice);
        tvRate = findViewById(R.id.detail_prate);
        tvStock = findViewById(R.id.detail_stock);
        tvTabProduct = findViewById(R.id.detail_tab_product);
        tvTabDetail = findViewById(R.id.detail_tab_detail);
        tvTabReview = findViewById(R.id.detail_tab_review);
        tvProductColor = findViewById(R.id.tab_product_color);
        tvProductSize = findViewById(R.id.tab_product_size);
        tvDetailCategory = findViewById(R.id.tab_detail_category);
        tvDetailBrand = findViewById(R.id.tab_detail_brand);
        tvDetailDesc = findViewById(R.id.tab_detail_desc);
        tabProductLayout = findViewById(R.id.tab_product_layout);
        tabDetailLayout = findViewById(R.id.tab_detail_layout);
        tabReviewLayout = findViewById(R.id.tab_review_layout);
        btnLayout = findViewById(R.id.detail_btn_layout);
        btnSave = findViewById(R.id.detail_btn_save);
        btnCart = findViewById(R.id.detail_btn_cart);
        reviewLayout = findViewById(R.id.review_layout);
        emptyReviewLayout = findViewById(R.id.empty_review_layout);

        detailLayout.setVisibility(View.GONE);
        getDetail(id);

        sizeList =new ArrayList<>();
        sizeAdapter = new DetailSizeAdapter(this, sizeList);
        layoutManager = new LinearLayoutManager(this);
        rvSize.setHasFixedSize(true);
        rvSize.setLayoutManager(layoutManager);

        colorList =new ArrayList<>();
        colorAdapter = new DetailColorAdapter(this, colorList);
        layoutManager = new LinearLayoutManager(this);
        rvColor.setHasFixedSize(true);
        rvColor.setLayoutManager(layoutManager);

        String cusid = Prefs.getString("mobile", "");

        if (cusid!=null && !cusid.isEmpty()){
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String timestamp = sdf.format(now);
            getCartFlag(cusid, id, timestamp);
            getBagFlag(cusid, id);
        }

        reviewList = new ArrayList<>();
        rvReview = findViewById(R.id.rv_detail_review);
        layoutManager = new LinearLayoutManager(this);
        rvReview.setLayoutManager(layoutManager);

        tvTabProduct.setTextColor(getResources().getColor(R.color.colorWhite));
        tvTabProduct.setBackgroundColor(getResources().getColor(R.color.colorOrange));

        tvTabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTabProduct.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTabProduct.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                tvTabDetail.setTextColor(getResources().getColor(R.color.colorGrey));
                tvTabDetail.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvTabReview.setTextColor(getResources().getColor(R.color.colorGrey));
                tvTabReview.setBackgroundColor(getResources().getColor(R.color.colorTransparent));

                tabProductLayout.setVisibility(View.VISIBLE);
                tabDetailLayout.setVisibility(View.GONE);
                tabReviewLayout.setVisibility(View.GONE);

                getDetail(id);
            }
        });

        tvTabDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTabDetail.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTabDetail.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                tvTabProduct.setTextColor(getResources().getColor(R.color.colorGrey));
                tvTabProduct.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvTabReview.setTextColor(getResources().getColor(R.color.colorGrey));
                tvTabReview.setBackgroundColor(getResources().getColor(R.color.colorTransparent));

                tabDetailLayout.setVisibility(View.VISIBLE);
                tabProductLayout.setVisibility(View.GONE);
                tabReviewLayout.setVisibility(View.GONE);

                btnLayout.setVisibility(View.GONE);
            }
        });

        tvTabReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTabReview.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTabReview.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                tvTabDetail.setTextColor(getResources().getColor(R.color.colorGrey));
                tvTabDetail.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvTabProduct.setTextColor(getResources().getColor(R.color.colorGrey));
                tvTabProduct.setBackgroundColor(getResources().getColor(R.color.colorTransparent));

                tabReviewLayout.setVisibility(View.VISIBLE);
                tabDetailLayout.setVisibility(View.GONE);
                tabProductLayout.setVisibility(View.GONE);

                btnLayout.setVisibility(View.GONE);

                getReview(id);
            }
        });

        cus_id = Prefs.getString("mobile", null);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Prefs.getBoolean("notLoggedIn", true)){
                    FBToast.infoToast(DetailActivity.this, "Login or Register to Proceed", FBToast.LENGTH_SHORT);
                }else {
                    Date now = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String timestamp = sdf.format(now);

                    if (Helper.cart.equals("0")) {
                        int flag = 1;
                        Helper.cart = "1";
                        btnCart.setText("REMOVE FROM CART");
                        Random rand = new Random();
                        int rand_int = rand.nextInt(1000);
                        addRemoveCart(id, cus_id, flag, timestamp, rand_int);
                    } else if (Helper.cart.equals("1")) {
                        int flag = 0;
                        Helper.cart = "0";
                        btnCart.setText("ADD TO CART");
                        Random rand = new Random();
                        int rand_int = rand.nextInt(1000);
                        addRemoveCart(id, cus_id, flag, timestamp, rand_int);
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String color = Prefs.getString("grabproductcolor", null);
                String size = Prefs.getString("grabproductsize", null);

                if (Helper.bag.equals("0")) {
                    int flag = 1;
                    Helper.bag = "1";
                    btnSave.setText("REMOVE FROM BAG");
                    addRemoveBag(id, cus_id, flag, color, size);
                } else if (Helper.bag.equals("1")) {
                    int flag = 0;
                    Helper.bag = "0";
                    btnSave.setText("ADD TO BAG");
                    addRemoveBag(id, cus_id, flag, color, size);
                }
            }
        });

    }

    private void getBagFlag(final String cusid, final String id) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, GET_BAG_FLAG_URL,
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

                                    String flag = jsonObject.getString("wish_flag");
                                    if (flag.equalsIgnoreCase("1")) {
                                        btnSave.setText("REMOVE FROM BAG");
                                        Helper.bag="1";
                                    }

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    btnSave.setText("ADD TO BAG");
                                    Helper.bag="0";
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                params.put("product_id", id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void addRemoveBag(final String id, final String cus_id, final int flag, final String color, final String size) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, BAG_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Inserted")){

                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.successToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.infoToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.successToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", id);
                params.put("flag", String.valueOf(flag));
                params.put("color", color);
                params.put("size", size);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getCartFlag(final String cusid, final String id, final String timestamp) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, GET_CART_FLAG_URL,
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

                                    String flag = jsonObject.getString("cart_flag");
                                    if (flag.equalsIgnoreCase("1")) {
                                        btnCart.setText("REMOVE FROM CART");
                                        Helper.cart="1";
                                    }

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    btnCart.setText("ADD TO CART");
                                    Helper.cart="0";
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cusid);
                params.put("product_id", id);
                params.put("date", timestamp);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void addRemoveCart(final String id, final String cus_id, final int flag, final String timestamp, final int rand_int) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, CART_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Inserted")){

                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.successToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.infoToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Deleted")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.successToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                b_id = Prefs.getString("bid", null);
                b_name = Prefs.getString("bname", null);
                b_mobile = Prefs.getString("bmobile", null);

                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", cus_id);
                params.put("product_id", id);
                params.put("flag", String.valueOf(flag));
                params.put("date", timestamp);
                params.put("bid", b_id);
                params.put("bname", b_name);
                params.put("bmobile", b_mobile);
                params.put("cartid", String.valueOf(rand_int));
                params.put("color", Prefs.getString("grabproductcolor", null));
                params.put("size", Prefs.getString("grabproductsize", null));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);

    }

    private void getReview(final String id) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, REVIEW_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    detailLayout.setVisibility(View.VISIBLE);
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    reviewList.clear();
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(0);

                                        map = new HashMap<String, String>();

                                        String name = object.getString("cus_name");
                                        String rating = object.getString("rating");
                                        String review = object.getString("review");
                                        String date = object.getString("rdate");

                                        map.put("name", name);
                                        map.put("rating", rating);
                                        map.put("review", review);
                                        map.put("date", date);

                                        reviewList.add(map);

                                    }

                                    reviewAdapter = new ReviewAdapter(DetailActivity.this, reviewList);
                                    rvReview.setAdapter(reviewAdapter);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    reviewLayout.setVisibility(View.GONE);
                                    emptyReviewLayout.setVisibility(View.VISIBLE);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.errorToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("product_id", id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getDetail(final String id) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    detailLayout.setVisibility(View.VISIBLE);
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    JSONObject object = array.getJSONObject(0);

                                    String barid = object.getString("barcode_id");
                                    String category = object.getString("sub_product");
                                    String brand = object.getString("model_name");
                                    String product = object.getString("product");
                                    String sliderimage = object.getString("image1");
                                    String price =  object.getString("price");
                                    String crossprice =  object.getString("cross_price");
                                    String prate =  object.getString("prate");
                                    String trate =  object.getString("trate");
                                    String size =  object.getString("size");
                                    String varcolor =  object.getString("all_color");
                                    String color =  object.getString("color");
                                    String desc =  object.getString("pro_desc");
                                    String branchid =  object.getString("b_id");
                                    String branchname =  object.getString("branchname");
                                    String branchmobile =  object.getString("b_mobile");
                                    String quantity =  object.getString("qty");

                                    Prefs.putString("bid", branchid);
                                    Prefs.putString("bname", branchname);
                                    Prefs.putString("bmobile" , branchmobile);

                                    if (sliderimage!=null && !sliderimage.isEmpty()){
                                        String [] list = sliderimage.split(",");
                                        List<String> sepList = Arrays.asList(list);
                                        ArrayList<String> imgList = new ArrayList<String>(sepList);
                                        NUM_PAGES = imgList.size();
                                        detailAdapter= new DetailAdapter(DetailActivity.this,imgList);
                                        viewPager.setAdapter(detailAdapter);
                                        viewPager.setOffscreenPageLimit(NUM_PAGES);
                                        pageIndicator.setViewPager(viewPager);
                                    }else {
                                        sliderimage = String.valueOf(R.drawable.image_alert);
                                        String [] list = sliderimage.split(",");
                                        List<String> sepList = Arrays.asList(list);
                                        ArrayList<String> imgList = new ArrayList<String>(sepList);
                                        NUM_PAGES = imgList.size();
                                        detailAdapter= new DetailAdapter(DetailActivity.this,imgList);
                                        viewPager.setAdapter(detailAdapter);
                                        viewPager.setOffscreenPageLimit(NUM_PAGES);
                                        pageIndicator.setViewPager(viewPager);
                                    }

                                    tvName.setText(product);
                                    tvPrice.setText("₹"+price);
                                    tvCrossPrice.setText("₹"+crossprice);
                                    tvCrossPrice.setPaintFlags(tvCrossPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                                    tvDetailCategory.setText(category);
                                    tvDetailBrand.setText(brand);

                                    if (desc!=null && !desc.isEmpty() && desc.trim().length() > 0) {
                                        tvDetailDesc.setText(Html.fromHtml(desc));
                                    }else {
                                        tvDetailDesc.setText("NA");
                                    }

                                    if (prate!=null && !prate.isEmpty() && prate.trim().length() > 0){
                                        tvRate.setText(prate);
                                    }else {
                                        tvRate.setText("0");
                                    }

                                    Prefs.putString("barid", barid);
                                    List<String> sepColor = Arrays.asList(varcolor.split("\\s*,\\s*"));

                                    if (varcolor!=null && !varcolor.isEmpty() && varcolor.trim().length() > 0) {
                                        colorLayout.setVisibility(View.VISIBLE);
                                        tvProductColor.setVisibility(View.GONE);
                                        sizeLayout.setVisibility(View.GONE);
                                        tvProductSize.setVisibility(View.VISIBLE);
                                        tvProductSize.setText("Select any one of the Available Color");
                                        colorList.clear();

                                        for (int i = 0; i < sepColor.size(); i++) {

                                            AllColor allColor = new AllColor(sepColor.get(i) + "");
                                            colorList.add(allColor);
                                        }

                                        Set<AllColor> set = new HashSet<>(colorList);
                                        colorList.clear();
                                        colorList.addAll(set);

                                        colorAdapter = new DetailColorAdapter(DetailActivity.this, colorList);
                                        rvColor.setAdapter(colorAdapter);
                                        colorAdapter.setOnColorChangeListener(DetailActivity.this, DetailActivity.this);
                                        colorAdapter.notifyDataSetChanged();
                                    } else if (color!=null && !color.isEmpty() && color.trim().length() > 0){

                                        colorLayout.setVisibility(View.VISIBLE);
                                        tvProductColor.setVisibility(View.GONE);
                                        sizeLayout.setVisibility(View.GONE);
                                        tvProductSize.setVisibility(View.VISIBLE);
                                        tvProductSize.setText("Select any one of the Available Color");
                                        colorList.clear();

                                        AllColor allColor = new AllColor(color);
                                        colorList.add(allColor);

                                        colorAdapter = new DetailColorAdapter(DetailActivity.this, colorList);
                                        rvColor.setAdapter(colorAdapter);
                                        colorAdapter.setOnColorChangeListener(DetailActivity.this, DetailActivity.this);
                                        colorAdapter.notifyDataSetChanged();
                                    } else {
                                        colorLayout.setVisibility(View.GONE);
                                        tvProductColor.setVisibility(View.VISIBLE);
                                        tvProductColor.setText("NA");
                                        getSize(barid, "NA");
                                    }

                                    if (!quantity.equalsIgnoreCase("0")){
                                        tvStock.setText("In Stock");
                                        tvStock.setTextColor(getResources().getColor(R.color.colorGreen));
                                    }else {
                                        tvStock.setText("Out Of Stock");
                                        tvStock.setTextColor(getResources().getColor(R.color.colorRed));
                                        btnLayout.setVisibility(View.GONE);
                                    }

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    detailLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.errorToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getSize(final String id, final String color) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, SIZE_URL,
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

                                    sizeLayout.setVisibility(View.VISIBLE);
                                    tvProductSize.setVisibility(View.GONE);

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);

                                    sizeList.clear();
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject object = array.getJSONObject(i);

                                        String size =  object.getString("size");

                                        sizeList.add(new AllSize(size));

                                    }

                                    sizeAdapter = new DetailSizeAdapter(DetailActivity.this, sizeList);
                                    rvSize.setAdapter(sizeAdapter);
                                    sizeAdapter.setOnSizeChangeListener(DetailActivity.this, DetailActivity.this);
                                    sizeAdapter.notifyDataSetChanged();

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    sizeLayout.setVisibility(View.GONE);
                                    tvProductSize.setVisibility(View.VISIBLE);
                                    tvProductSize.setText("NA");

                                    btnLayout.setVisibility(View.VISIBLE);

                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.errorToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", id);
                params.put("color", color);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);

    }

    private void getColorSizeProduct(final String detbarid, final String detcolor, final String detsize) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, GET_COLOR_SIZE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("success")){
                                    detailLayout.setVisibility(View.VISIBLE);
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    JSONObject object = array.getJSONObject(0);

                                    String id = object.getString("id");

                                    Prefs.putString("did", id);

                                    //FBToast.infoToast(GrabOfferActivity.this, id, FBToast.LENGTH_SHORT);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    detailLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.errorToast(DetailActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(DetailActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(DetailActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                        FBToast.errorToast(DetailActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("barid", detbarid);
                params.put("color", detcolor);
                params.put("size", detsize);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.home_menu, menu);
        showOption(R.id.action_cart);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            startActivity(new Intent(DetailActivity.this, CartActivity.class));
            Bungee.swipeRight(DetailActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DetailActivity.this, ProductActivity.class));
        Bungee.swipeLeft(DetailActivity.this);
    }

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
    public void onColorChanged(String color) {

        String bid = Prefs.getString("barid", null);
        getSize(bid, color);
        Prefs.putString("grabproductcolor", color);
    }



    @Override
    public void onSizeChanged(String size) {

        Prefs.putString("grabproductsize", size);

        String detbarid = Prefs.getString("barid", null);
        String detcolor = Prefs.getString("grabproductcolor", null);
        String detsize = Prefs.getString("grabproductsize", null);

        getColorSizeProduct(detbarid, detcolor, detsize);
    }
}
