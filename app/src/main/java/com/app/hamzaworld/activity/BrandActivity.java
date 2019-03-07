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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.hamzaworld.R;
import com.app.hamzaworld.adapter.BrandAdapter;
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

public class BrandActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    UberProgressView progress;
    String cat, subCat;
    Menu menu;
    RecyclerView rvBrand;
    LinearLayout brandLayout, emptyLayout;
    BrandAdapter brandAdapter;
    ArrayList<HashMap<String, String>> brandList;
    HashMap<String, String> map;
    RecyclerView.LayoutManager layoutManager;
    String BRAND_URL = Helper.BASE_URL + Helper.GET_BRAND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        Intent intent = getIntent();
        cat = intent.getStringExtra("cat");
        subCat = intent.getStringExtra("subcat");

        if (subCat == null && cat == null){
            cat = Prefs.getString("bcategory", null);
            subCat = Prefs.getString("bsubcategory", null);
        }else {
            Prefs.putString("bcategory", cat);
            Prefs.putString("bsubcategory", subCat);
        }

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText(subCat);
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

        progress = findViewById(R.id.brand_progress);

        brandLayout = findViewById(R.id.brand_layout);
        emptyLayout = findViewById(R.id.brand_empty_layout);

        rvBrand = findViewById(R.id.rv_subcategory_brand);
        brandList =new ArrayList<>();
        brandAdapter = new BrandAdapter(this, brandList);
        layoutManager = new GridLayoutManager(this, 2);
        rvBrand.setHasFixedSize(true);
        rvBrand.setNestedScrollingEnabled(false);
        rvBrand.setLayoutManager(layoutManager);

        getBrand(cat, subCat);
    }

    private void getBrand(final String category, final String subCategory) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, BRAND_URL,
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
                                        String category = object.getString("item");
                                        String subcategory = object.getString("sub_product");
                                        String brand = object.getString("model_name");
                                        String proimage = object.getString("image");

                                        map.put("id", id);
                                        map.put("brand", brand);
                                        map.put("proimage", proimage);
                                        map.put("cat", category);
                                        map.put("subcat", subcategory);


                                        brandList.add(map);

                                    }

                                    brandAdapter = new BrandAdapter(BrandActivity.this, brandList);
                                    rvBrand.setAdapter(brandAdapter);

                                }else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("empty")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    brandLayout.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                }
                                else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(BrandActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(BrandActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(BrandActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(BrandActivity.this);
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
                        FBToast.errorToast(BrandActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("cat", category);
                params.put("subcat", subCategory);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(BrandActivity.this);
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
            startActivity(new Intent(BrandActivity.this, CartActivity.class));
            Bungee.swipeRight(BrandActivity.this);
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
        startActivity(new Intent(BrandActivity.this, CategoryActivity.class));
        Bungee.swipeLeft(BrandActivity.this);
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
