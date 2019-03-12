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
import android.text.TextUtils;
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
import com.app.hamzaworld.adapter.BagAdapter;
import com.app.hamzaworld.other.HamzaWorld;
import com.app.hamzaworld.other.Helper;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ishankhanna.UberProgressView;
import spencerstudios.com.bungeelib.Bungee;

public class BagActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    public static UberProgressView progress;
    RecyclerView rvWish;
    Button btnAdd;
    HashMap<String, String> map;
    RecyclerView.LayoutManager layoutManager;
    BagAdapter bagAdapter;
    public static ArrayList<HashMap<String,String>> bagList;
    public static LinearLayout emptyLayout, bagLayout;
    String BAG_URL = Helper.BASE_URL + Helper.GET_WISHLIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MY BAG");
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

        progress = findViewById(R.id.bag_progress);
        rvWish = findViewById(R.id.rv_bag);
        emptyLayout = findViewById(R.id.bag_empty_layout);
        bagLayout = findViewById(R.id.bag_layout);
        btnAdd = findViewById(R.id.bag_btn_add);

        bagList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        rvWish.setLayoutManager(layoutManager);

        String mobile = Prefs.getString("mobile", "");
        getBag(mobile);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(BagActivity.this, HomeActivity.class));
                Bungee.shrink(BagActivity.this);
            }
        });
    }

    private void getBag(final String mobile) {

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
                                        .equalsIgnoreCase("success")){

                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    bagLayout.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);

                                    String data = jsonObject.getString("data");
                                    JSONArray array = new JSONArray(data);
                                    bagList.clear();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        map = new HashMap<String, String>();

                                        String id = object.getString("p_id");
                                        String category = object.getString("sub_product");
                                        String product = object.getString("product");
                                        String pro_image = object.getString("image");
                                        String price =  object.getString("price");
                                        String crossprice =  object.getString("cross_price");
                                        String prate =  object.getString("prate");
                                        String trate =  object.getString("trate");
                                        String color =  object.getString("bcolor");
                                        String size =  object.getString("bsize");

                                        map.put("id", id);
                                        map.put("category", category);
                                        map.put("product", product);
                                        map.put("pro_image", pro_image);
                                        map.put("price", price);
                                        map.put("cross_price", crossprice);
                                        map.put("prate", prate);
                                        map.put("trate", trate);
                                        map.put("color", color);
                                        map.put("size", size);

                                        bagList.add(map);

                                    }
                                    bagAdapter = new BagAdapter(BagActivity.this, bagList);
                                    rvWish.setAdapter(bagAdapter);


                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    bagLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(BagActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(BagActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(BagActivity.this);
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
                        FBToast.errorToast(BagActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("customer_id", mobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(BagActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    public static void bagList(){
        if (bagList.size()==0){
            bagLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else {
            bagLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(BagActivity.this, MoreActivity.class));
        Bungee.swipeLeft(BagActivity.this);
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
