package com.app.hamzaworld.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
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
import com.app.hamzaworld.other.CustomFont;
import com.app.hamzaworld.other.HamzaWorld;
import com.app.hamzaworld.other.Helper;
import com.arvind.otpview.OTPView;
import com.arvind.otpview.OnCompleteListener;
import com.libizo.CustomEditText;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import in.ishankhanna.UberProgressView;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class ProfileActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    UberProgressView progress;
    Menu menu;
    AlertDialog generateDialog, verifyDialog;
    ValidUtils validUtils;
    CustomEditText etRegMobile, etRegPass, etRegCPass, etLogMobile, etLogPass, etUserName,
    etUserMobile, etUserEmail, etUserAdd1, etUserAdd2, etUserCity, etUserState, etUserPincode;
    BottomNavigationView navigation;
    TextView tvName, tvEmail, tvMobile, tvAdd1, tvAdd2, tvCity, tvState, tvPincode;
    ScrollView profileLayout, registerLayout, updateLayout;
    LinearLayout startLayout, loginLayout;
    Button btnLogin, btnRegister, btnRegRegister, btnLogLogin , btnSave, btnEdit;
    String REGISTER_URL = Helper.BASE_URL + Helper.REGISTER_USER;
    String CHECK_URL = Helper.BASE_URL + Helper.CHECK_REGISTER;
    String LOGIN_URL = Helper.BASE_URL + Helper.LOGIN_USER;
    String GENERATE_URL = Helper.BASE_URL + Helper.GENERATE_OTP;
    String VERIFY_URL = Helper.BASE_URL + Helper.VERIFY_OTP;
    String UPDATE_URL = Helper.BASE_URL + Helper.UPDATE_PROFILE;
    String PROFILE_URL = Helper.BASE_URL + Helper.GET_PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("PROFILE");
        title.setTextSize(23);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_profile).setChecked(true);

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

        progress = findViewById(R.id.profile_progress);
        profileLayout = findViewById(R.id.profile_layout);
        startLayout = findViewById(R.id.start_layout);
        registerLayout = findViewById(R.id.register_layout);
        btnLogin = findViewById(R.id.profile_btn_login);
        btnRegister = findViewById(R.id.profile_btn_register);
        btnRegRegister = findViewById(R.id.reg_btn_register);
        etRegMobile = findViewById(R.id.reg_et_mobile);
        etRegPass = findViewById(R.id.reg_et_pass);
        etRegCPass = findViewById(R.id.reg_et_cpass);
        etLogMobile = findViewById(R.id.log_et_phone);
        etLogPass = findViewById(R.id.log_et_pass);
        btnLogLogin = findViewById(R.id.log_btn_login);
        loginLayout = findViewById(R.id.login_layout);
        etUserName = findViewById(R.id.user_et_name);
        etUserMobile = findViewById(R.id.user_et_mobile);
        etUserEmail = findViewById(R.id.user_et_email);
        etUserAdd1 = findViewById(R.id.user_et_address1);
        etUserAdd2 = findViewById(R.id.user_et_address2);
        etUserCity = findViewById(R.id.user_et_city);
        etUserState = findViewById(R.id.user_et_state);
        etUserPincode = findViewById(R.id.user_et_pincode);
        btnSave = findViewById(R.id.user_btn_save);
        updateLayout = findViewById(R.id.update_layout);
        tvName = findViewById(R.id.profile_tv_name);
        tvEmail = findViewById(R.id.profile_tv_email);
        tvMobile = findViewById(R.id.profile_tv_mobile);
        tvAdd1 = findViewById(R.id.profile_tv_address1);
        tvAdd2 = findViewById(R.id.profile_tv_address2);
        tvCity = findViewById(R.id.profile_tv_city);
        tvState = findViewById(R.id.profile_tv_state);
        tvPincode = findViewById(R.id.profile_tv_pincode);
        btnEdit = findViewById(R.id.profile_btn_edit);

        if (Prefs.getBoolean("notLoggedIn", true)){
            startLayout.setVisibility(View.VISIBLE);
            updateLayout.setVisibility(View.GONE);
        }else {
            startLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
            String mobile = Prefs.getString("mobile", null);
            getProfile(mobile);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validUtils.validateEditTexts(etUserName, etUserMobile, etUserEmail,
                        etUserAdd1, etUserAdd2, etUserCity, etUserState, etUserPincode)){

                    String name = etUserName.getText().toString().trim();
                    String mobile = etUserMobile.getText().toString().trim();
                    String email = etUserEmail.getText().toString().trim();
                    String add1 = etUserAdd1.getText().toString().trim();
                    String add2 = etUserAdd2.getText().toString().trim();
                    String city = etUserCity.getText().toString().trim();
                    String state = etUserState.getText().toString().trim();
                    String pincode = etUserPincode.getText().toString().trim();

                    update(name, mobile, email, add1, add2, city, state, pincode);
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileLayout.setVisibility(View.GONE);
                updateLayout.setVisibility(View.VISIBLE);

                etUserName.setText(Prefs.getString("uname", null));
                etUserEmail.setText(Prefs.getString("uemail", null));
                etUserMobile.setText(Prefs.getString("umobile", null));
                etUserAdd1.setText(Prefs.getString("uadd1", null));
                etUserAdd2.setText(Prefs.getString("uadd2", null));
                etUserCity.setText(Prefs.getString("ucity", null));
                etUserState.setText(Prefs.getString("ustate", null));
                etUserPincode.setText(Prefs.getString("upincode", null));
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.GONE);
                updateLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);

                btnRegRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validUtils.validateEditTexts(etRegMobile, etRegPass, etRegCPass)){

                            String mobile = etRegMobile.getText().toString().trim();
                            String pass = etRegPass.getText().toString().trim();
                            String cpass = etRegCPass.getText().toString().trim();

                            if (pass.matches(cpass)){
                                checkUser(mobile, cpass);
                            }else {
                                FBToast.warningToast(getApplicationContext(),"Password didn't Match!",FBToast.LENGTH_SHORT);
                            }
                        }else {
                            FBToast.warningToast(getApplicationContext(),"Empty Feilds",FBToast.LENGTH_SHORT);
                        }
                    }
                });

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginLayout.setVisibility(View.VISIBLE);
                startLayout.setVisibility(View.GONE);
                profileLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.GONE);
                updateLayout.setVisibility(View.GONE);

                btnLogLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validUtils.validateEditTexts(etLogMobile, etLogPass)){

                            String mobile = etLogMobile.getText().toString().trim();
                            String pass = etLogPass.getText().toString().trim();
                            login(mobile, pass);

                        }else {
                            FBToast.warningToast(getApplicationContext(),"Empty Feilds",FBToast.LENGTH_SHORT);
                        }
                    }
                });

            }
        });

    }

    private void update(final String name, final String mobile, final String email, final String add1, final String add2, final String city, final String state, final String pincode) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
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

                                    profileLayout.setVisibility(View.VISIBLE);
                                    updateLayout.setVisibility(View.GONE);

                                    getProfile(mobile);

                                    Prefs.putString("uname", name);
                                    Prefs.putString("uemail", email);
                                    Prefs.putString("umobile", mobile);
                                    Prefs.putString("uadd1", add1);
                                    Prefs.putString("uadd2", add2);
                                    Prefs.putString("ucity", city);
                                    Prefs.putString("ustate", state);
                                    Prefs.putString("upincode", pincode);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(ProfileActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(ProfileActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("city", city);
                params.put("state", state);
                params.put("pincode", pincode);
                params.put("address1", add1);
                params.put("address2", add2);
                params.put("mobileno", mobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void getProfile(final String mobile) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, PROFILE_URL,
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
                                    JSONObject object = array.getJSONObject(0);

                                    String cusid = object.getString("cus_id");
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    String mobile = object.getString("mobile_no");
                                    String add1 = object.getString("address1");
                                    String add2 = object.getString("address2");
                                    String city = object.getString("city");
                                    String state = object.getString("state");
                                    String pincode = object.getString("post_code");

                                    tvName.setText(name);
                                    tvMobile.setText(mobile);
                                    tvEmail.setText(email);
                                    tvAdd1.setText(add1);
                                    tvAdd2.setText(add2);
                                    tvCity.setText(city);
                                    tvState.setText(state);
                                    tvPincode.setText(pincode);

                                    Prefs.putString("mobile", mobile);

                                    Prefs.putString("uid", cusid);
                                    Prefs.putString("uname", name);
                                    Prefs.putString("uemail", email);
                                    Prefs.putString("umobile", mobile);
                                    Prefs.putString("uadd1", add1);
                                    Prefs.putString("uadd2", add2);
                                    Prefs.putString("ucity", city);
                                    Prefs.putString("ustate", state);
                                    Prefs.putString("upincode", pincode);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(ProfileActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(ProfileActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", mobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);

    }

    private void login(final String mobile, final String pass) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, LOGIN_URL,
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

                                    Prefs.putBoolean("notLoggedIn", false);
                                    Prefs.putString("mobile", mobile);
                                    Prefs.putString("password", pass);

                                    if (Prefs.getBoolean("notLoggedIn", true)){
                                        startLayout.setVisibility(View.GONE);
                                        profileLayout.setVisibility(View.GONE);
                                        registerLayout.setVisibility(View.GONE);
                                        loginLayout.setVisibility(View.GONE);
                                        updateLayout.setVisibility(View.VISIBLE);
                                    }else {
                                        startLayout.setVisibility(View.GONE);
                                        profileLayout.setVisibility(View.VISIBLE);
                                        registerLayout.setVisibility(View.GONE);
                                        loginLayout.setVisibility(View.GONE);
                                        updateLayout.setVisibility(View.GONE);
                                        getProfile(mobile);
                                    }


                                    etUserMobile.setText(Prefs.getString("mobile", null));

                                    FBToast.successToast(ProfileActivity.this, "Logged In Successfully", FBToast.LENGTH_SHORT);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(ProfileActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(ProfileActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", mobile);
                params.put("password", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void checkUser(final String mobile,final String pass) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, CHECK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject != null){

                                if (jsonObject.getString("status")
                                        .equalsIgnoreCase("Already")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.warningToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("New User")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                                    LayoutInflater inflater = getLayoutInflater();
                                    final View view = inflater.inflate(R.layout.generate_dialog, null);

                                    final CustomEditText etMobile = view.findViewById(R.id.generate_et_mobile);
                                    Button btnGenerate = view.findViewById(R.id.generate_btn);

                                    builder.setCancelable(false);
                                    builder.setView(view);
                                    generateDialog = builder.create();

                                    etMobile.setText(mobile);

                                    btnGenerate.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String mob = etMobile.getText().toString().trim();
                                            generate(mob, pass);


                                        }
                                    });

                                    generateDialog.show();

                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(ProfileActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(ProfileActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", mobile);
                params.put("password", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void generate(final String mobile, final String pass) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, GENERATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        generateDialog.dismiss();

                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.verify_dialog, null);

                        OTPView otpView = view.findViewById(R.id.verify_otp);

                        builder.setCancelable(false);
                        builder.setView(view);
                        verifyDialog = builder.create();

                        otpView.setListener(new OnCompleteListener() {
                            @Override
                            public void onOTPComplete(String otp) {

                                verify(mobile, otp, pass);

                            }
                        });

                        verifyDialog.show();

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        //FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("password", pass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void verify(final String mobile, final String otp, final String pass) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, VERIFY_URL,
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

                                    verifyDialog.dismiss();

                                    register(mobile, pass);

                                    FBToast.successToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(ProfileActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(ProfileActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("otp", otp);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    private void register(final String mobile, final String cpass) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, REGISTER_URL,
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

                                    startLayout.setVisibility(View.GONE);
                                    profileLayout.setVisibility(View.GONE);
                                    registerLayout.setVisibility(View.GONE);
                                    loginLayout.setVisibility(View.VISIBLE);

                                    btnLogLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (validUtils.validateEditTexts(etLogMobile, etLogPass)){

                                                String mobile = etLogMobile.getText().toString().trim();
                                                String pass = etLogPass.getText().toString().trim();
                                                login(mobile, pass);

                                            }else {
                                                FBToast.warningToast(getApplicationContext(),"Empty Feilds",FBToast.LENGTH_SHORT);
                                            }
                                        }
                                    });

                                    FBToast.successToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(ProfileActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(ProfileActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(ProfileActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                        FBToast.errorToast(ProfileActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("mobileno", mobile);
                params.put("password", cpass);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
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
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    Bungee.fade(ProfileActivity.this);
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                    Bungee.fade(ProfileActivity.this);
                    return true;
                case R.id.navigation_more:
                    startActivity(new Intent(ProfileActivity.this, MoreActivity.class));
                    Bungee.fade(ProfileActivity.this);
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
            startActivity(new Intent(ProfileActivity.this, CartActivity.class));
            Bungee.swipeRight(ProfileActivity.this);
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
