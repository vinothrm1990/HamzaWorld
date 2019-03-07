package com.app.hamzaworld.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.hamzaworld.R;
import com.app.hamzaworld.adapter.CategoryAdapter;
import com.app.hamzaworld.adapter.GrabOfferAdapter;
import com.app.hamzaworld.adapter.GrabProductAdapter;
import com.app.hamzaworld.data.Category;
import com.app.hamzaworld.other.CustomFont;
import com.app.hamzaworld.other.HamzaWorld;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import com.tfb.fbtoast.FBToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import in.ishankhanna.UberProgressView;
import spencerstudios.com.bungeelib.Bungee;

public class HomeActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    Menu menu;
    UberProgressView progress;
    HashMap<String, String> map, map1;
    NetworkImageView nivBanner1, nivBanner2, nivBanner3;
    long diff, oldLong, newLong;
    TextView timer;
    LinearLayout offerLayout;
    ArrayList<HashMap<String, String>> offerList, productList;
    ArrayList<String> bannerList;
    List<Category> categoryList;
    RecyclerView rvCategory, rvOffer, rvProduct;
    CategoryAdapter catAdapter;
    GrabProductAdapter grabProductAdapter;
    GrabOfferAdapter grabOfferAdapter;
    ImageLoader imageLoader;
    RecyclerView.LayoutManager layoutManager;
    BottomNavigationView navigation;
    String BANNER_URL = Helper.BASE_URL + Helper.GET_BANNER;
    String OFFER_URL = Helper.BASE_URL + Helper.GET_OFFER;
    String TIMER_URL = Helper.BASE_URL + Helper.GET_TIMER;
    String PRODUCT_URL = Helper.BASE_URL + Helper.GET_NEW_PRODUCT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("HAMZA WORLD");
        title.setTextSize(23);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);

        Menu menu = navigation.getMenu();

        for (int i=0;i<menu.size();i++) {

            MenuItem menuItem = menu.getItem(i);

            SubMenu subMenu = menuItem.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    fontMenu(subMenuItem);
                }
            }
            fontMenu(menuItem);
        }

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);

        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams params = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
            params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
            iconView.setLayoutParams(params);
        }

        progress = findViewById(R.id.home_progress);

        timer = findViewById(R.id.offer_timer);
        offerLayout = findViewById(R.id.offer_layout);
        nivBanner1 = findViewById(R.id.banner1);
        nivBanner2 = findViewById(R.id.banner2);
        nivBanner3 = findViewById(R.id.banner3);

        rvCategory = findViewById(R.id.rv_home_cat);
        categoryList = new ArrayList<>();
        catAdapter = new CategoryAdapter(this, categoryList);
        layoutManager = new GridLayoutManager(this, 4);
        rvCategory.setHasFixedSize(true);
        rvCategory.setNestedScrollingEnabled(false);
        rvCategory.setLayoutManager(layoutManager);
        rvCategory.setAdapter(catAdapter);

        rvOffer = findViewById(R.id.rv_home_offer);
        offerList = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        rvOffer.setHasFixedSize(true);
        rvOffer.setNestedScrollingEnabled(false);
        rvOffer.setLayoutManager(layoutManager);

        rvProduct = findViewById(R.id.rv_home_product);
        productList = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2);
        rvProduct.setNestedScrollingEnabled(false);
        rvProduct.setLayoutManager(layoutManager);

        bannerList = new ArrayList<>();

        getCategory();
        getBanner();
        getOffer();
        getTimer();
        getProduct();

    }

    private void getProduct() {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.GET, PRODUCT_URL,
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

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);

                                        map1 = new HashMap<>();

                                        String id = object.getString("id");
                                        String product = object.getString("product");
                                        String image = object.getString("image");
                                        String price = object.getString("price");
                                        String cprice = object.getString("cross_price");

                                        map1.put("id", id);
                                        map1.put("product", product);
                                        map1.put("image", image);
                                        map1.put("price", price);
                                        map1.put("cross_price", cprice);

                                        productList.add(map1);
                                    }

                                    grabProductAdapter = new GrabProductAdapter(HomeActivity.this, productList);
                                    rvProduct.setAdapter(grabProductAdapter);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    timer.setText("OFFER ENDS");
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(HomeActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(HomeActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(HomeActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                        FBToast.errorToast(HomeActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getTimer() {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.GET, TIMER_URL,
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

                                    String months = jsonObject.getString("months");
                                    String days = jsonObject.getString("days");
                                    String years = jsonObject.getString("years");
                                    String hours = jsonObject.getString("hours");
                                    String minutes = jsonObject.getString("minutes");
                                    String seconds = jsonObject.getString("seconds");

                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");

                                    Date now = Calendar.getInstance().getTime();

                                    String cDate = simpleDateFormat.format(now);

                                    String oDate = months+"  "+days+",  "+years+"  "+hours+":"+minutes+":"+seconds;

                                    Date old = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss").parse(oDate);

                                    String gDate = simpleDateFormat.format(old);

                                    Date oldDate, newDate;

                                    try {
                                        oldDate = simpleDateFormat.parse(cDate);
                                        newDate = simpleDateFormat.parse(gDate);
                                        oldLong = oldDate.getTime();
                                        newLong = newDate.getTime();
                                        diff = newLong - oldLong;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    new CountDownTimer(diff, 1000){

                                        public void onTick(long millisUntilFinished) {
                                            long millis = millisUntilFinished;
                                            String hms = (TimeUnit.MILLISECONDS.toDays(millis))+"Days "
                                                    + "["+ (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)) + ":")
                                                    + (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) + ":"
                                                    + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) +"]"));
                                            timer.setText("ENDS IN :\t" + hms);
                                            //here you can have your logic to set text to edittext
                                        }

                                        public void onFinish() {
                                            timer.setText("OFFER ENDS");
                                            offerLayout.setVisibility(View.GONE);
                                        }

                                    }.start();



                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    timer.setText("OFFER ENDS");
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(HomeActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(HomeActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(HomeActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                        FBToast.errorToast(HomeActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getOffer() {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.GET, OFFER_URL,
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

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);

                                        map = new HashMap<>();

                                        String id = object.getString("id");
                                        String product = object.getString("product");
                                        String image = object.getString("image");
                                        String price = object.getString("price");
                                        String cprice = object.getString("cross_price");

                                        map.put("id", id);
                                        map.put("product", product);
                                        map.put("image", image);
                                        map.put("price", price);
                                        map.put("cross_price", cprice);

                                        offerList.add(map);

                                    }

                                    grabOfferAdapter = new GrabOfferAdapter(HomeActivity.this, offerList);
                                    rvOffer.setAdapter(grabOfferAdapter);


                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    offerLayout.setVisibility(View.GONE);
                                    //FBToast.warningToast(HomeActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(HomeActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(HomeActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(HomeActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                        FBToast.errorToast(HomeActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getBanner() {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.GET, BANNER_URL,
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

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);

                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);

                                        String image = object.getString("img");

                                        bannerList.add(image);

                                    }

                                    imageLoader = ImageCache.getInstance(HomeActivity.this).getImageLoader();

                                    imageLoader.get(Helper.BANNER_URL + bannerList.get(0), ImageLoader.getImageListener(nivBanner1, R.drawable.image_preview, R.drawable.image_alert));
                                    nivBanner1.setImageUrl(Helper.BANNER_URL + bannerList.get(0), imageLoader);
                                    nivBanner1.setScaleType(NetworkImageView.ScaleType.FIT_XY);

                                    imageLoader.get(Helper.BANNER_URL + bannerList.get(1), ImageLoader.getImageListener(nivBanner2, R.drawable.image_preview, R.drawable.image_alert));
                                    nivBanner2.setImageUrl(Helper.BANNER_URL + bannerList.get(1), imageLoader);
                                    nivBanner2.setScaleType(NetworkImageView.ScaleType.FIT_XY);

                                    imageLoader.get(Helper.BANNER_URL + bannerList.get(2), ImageLoader.getImageListener(nivBanner3, R.drawable.image_preview, R.drawable.image_alert));
                                    nivBanner3.setImageUrl(Helper.BANNER_URL + bannerList.get(2), imageLoader);
                                    nivBanner3.setScaleType(NetworkImageView.ScaleType.FIT_XY);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.warningToast(HomeActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.errorToast(HomeActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(HomeActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(HomeActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                        FBToast.errorToast(HomeActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getCategory() {

        Category menu = new Category("Clothings", R.drawable.clothing);
        categoryList.add(menu);
        menu = new Category("Footwear", R.drawable.footwear);
        categoryList.add(menu);
        menu = new Category("Beauty", R.drawable.beauty);
        categoryList.add(menu);
        menu = new Category("See All", R.drawable.all);
        categoryList.add(menu);

        catAdapter.notifyDataSetChanged();
    }

    private void fontMenu(MenuItem menuItem) {
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
        mNewTitle.setSpan(new CustomFont("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            for (int i = 0; i < navigation.getMenu().size(); i++) {
                MenuItem menuItem = navigation.getMenu().getItem(i);
                boolean isChecked = menuItem.getItemId() == item.getItemId();
                menuItem.setChecked(isChecked);
            }

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                    Bungee.fade(HomeActivity.this);
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    Bungee.fade(HomeActivity.this);
                    return true;
                case R.id.navigation_more:
                    startActivity(new Intent(HomeActivity.this, MoreActivity.class));
                    Bungee.fade(HomeActivity.this);
                    return true;
            }
            return false;
        }
    };

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
            startActivity(new Intent(HomeActivity.this, CartActivity.class));
            Bungee.swipeRight(HomeActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
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
}
