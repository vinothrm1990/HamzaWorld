package com.app.hamzaworld.activity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.hamzaworld.adapter.ReviewAdapter;
import com.app.hamzaworld.other.HamzaWorld;
import com.app.hamzaworld.other.Helper;
import com.easebuzz.payment.kit.PWECouponsActivity;
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
import java.util.Random;

import datamodels.StaticDataModel;
import in.ishankhanna.UberProgressView;
import spencerstudios.com.bungeelib.Bungee;
import thebat.lib.validutil.ValidUtils;

public class CheckoutActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    TextView tvName, tvMobile, tvAdd1, tvAdd2, tvCity, tvState, tvPincode, tvTotal;
    Button btnPlace;
    ImageView ivChange;
    ValidUtils validUtils;
    RadioButton rbCash, rbOnline;
    String total;
    UberProgressView progress;
    AlertDialog cashDialog, onlineDialog, changeDialog;
    String PLACE_URL = Helper.BASE_URL + Helper.PLACE_ORDER;
    String PAYMENT_URL = Helper.BASE_URL + Helper.PAYMENT_DETAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);
        validUtils = new ValidUtils();

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("Checkout");
        title.setTextSize(23);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(title);

        Intent intent = getIntent();
        total = intent.getStringExtra("total");

        if (total == null){
            total = Prefs.getString("ctotal", null);
        }else {
            Prefs.putString("ctotal", total);
        }

        progress = findViewById(R.id.checkout_progress);
        tvName = findViewById(R.id.check_tv_name);
        tvMobile = findViewById(R.id.check_tv_mobile);
        tvAdd1 = findViewById(R.id.check_tv_add1);
        tvAdd2 = findViewById(R.id.check_tv_add2);
        tvCity = findViewById(R.id.check_tv_city);
        tvState = findViewById(R.id.check_tv_state);
        tvPincode = findViewById(R.id.check_tv_pincode);
        tvTotal = findViewById(R.id.check_total);
        btnPlace = findViewById(R.id.check_btn_place);
        ivChange = findViewById(R.id.check_iv_change);
        rbCash = findViewById(R.id.check_rb_cash);
        rbOnline = findViewById(R.id.check_rb_online);

        tvName.setText(Prefs.getString("uname", null));
        tvAdd1.setText(Prefs.getString("uadd1", null));
        tvAdd2.setText(Prefs.getString("uadd2", null));
        tvCity.setText(Prefs.getString("ucity", null));
        tvState.setText(Prefs.getString("ustate", null));
        tvPincode.setText(Prefs.getString("upincode", null));
        tvMobile.setText(Prefs.getString("umobile", null));
        tvTotal.setText("₹"+total);

        ivChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.change_dialog, null);

                final CustomEditText etName = view.findViewById(R.id.change_et_name);
                final CustomEditText etMobile = view.findViewById(R.id.change_et_mobile);
                final CustomEditText etAdd1 = view.findViewById(R.id.change_et_add1);
                final CustomEditText etAdd2 = view.findViewById(R.id.change_et_add2);
                final CustomEditText etCity = view.findViewById(R.id.change_et_city);
                final CustomEditText etState = view.findViewById(R.id.change_et_state);
                final CustomEditText etPincode = view.findViewById(R.id.change_et_pincode);
                Button btnUpdate = view.findViewById(R.id.change_btn_update);

                builder.setView(view);

                etName.setText(tvName.getText().toString().trim());
                etAdd1.setText(tvAdd1.getText().toString().trim());
                etAdd2.setText(tvAdd2.getText().toString().trim());
                etCity.setText(tvCity.getText().toString().trim());
                etState.setText(tvState.getText().toString().trim());
                etPincode.setText(tvPincode.getText().toString().trim());
                etMobile.setText(tvMobile.getText().toString().trim());

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validUtils.validateEditTexts(etName, etMobile, etAdd1, etAdd2, etCity, etState, etPincode)){

                            String name = etName.getText().toString().trim();
                            String mobile = etMobile.getText().toString().trim();
                            String add1 = etAdd1.getText().toString().trim();
                            String add2 = etAdd2.getText().toString().trim();
                            String city = etCity.getText().toString().trim();
                            String state = etState.getText().toString().trim();
                            String pincode = etPincode.getText().toString().trim();

                            tvName.setText(name);
                            tvMobile.setText(mobile);
                            tvAdd1.setText(add1);
                            tvAdd2.setText(add2);
                            tvCity.setText(city);
                            tvState.setText(state);
                            tvPincode.setText(pincode);

                            changeDialog.dismiss();
                        }

                    }
                });

                changeDialog = builder.create();
                changeDialog.show();

            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!rbCash.isChecked() && !rbOnline.isChecked()){
                    FBToast.infoToast(CheckoutActivity.this, "Select any one of the Payment Method", FBToast.LENGTH_SHORT);
                }else {

                    if (rbCash.isChecked()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("You have selected Cash on Delivery Mode");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String cusid = Prefs.getString("uid", null);
                                String email = Prefs.getString("uemail", null);
                                String mobile = Prefs.getString("umobile", null);
                                String cname = tvName.getText().toString().trim();
                                String cmobile = tvMobile.getText().toString().trim();
                                String cadd1 = tvAdd1.getText().toString().trim();
                                String cadd2 = tvAdd2.getText().toString().trim();
                                String ccity = tvCity.getText().toString().trim();
                                String cstate = tvState.getText().toString().trim();
                                String cpincode = tvPincode.getText().toString().trim();
                                String mode = "Cash";
                                place(cusid, cname, email, mobile, cadd1, cadd2, ccity, cstate, cpincode, mode, cmobile);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cashDialog.dismiss();
                            }
                        });

                        cashDialog = builder.create();
                        cashDialog.show();
                    }else if (rbOnline.isChecked()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("You have selected Online Payment Mode");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int random = (int)(Math.random() * 1000 + 1);

                                float amount = Float.valueOf(total);
                                String pinfo = "HamzaProduct"+random;
                                String name = tvName.getText().toString().trim();
                                String email = Prefs.getString("uemail", null);
                                String mobile = tvMobile.getText().toString().trim();
                                String add1 = tvAdd1.getText().toString().trim();
                                String add2 = tvAdd2.getText().toString().trim();
                                String city = tvCity.getText().toString().trim();
                                String state = tvState.getText().toString().trim();
                                String country = "India";
                                String pincode = tvPincode.getText().toString().trim();

                                Intent intent = new Intent(CheckoutActivity.this, PWECouponsActivity.class);
                                intent.putExtra("trxn_id", Helper.TRANSACTION_ID);
                                intent.putExtra("trxn_amount", amount);
                                intent.putExtra("trxn_prod_info", pinfo);
                                intent.putExtra("trxn_firstname", name);
                                intent.putExtra("trxn_email_id", email);
                                intent.putExtra("trxn_phone", mobile);
                                intent.putExtra("trxn_key", Helper.MERCHANT_KEY);
                                intent.putExtra("trxn_udf1","abc");
                                intent.putExtra("trxn_udf2","def");
                                intent.putExtra("trxn_udf3","ghi");
                                intent.putExtra("trxn_udf4","jkl");
                                intent.putExtra("trxn_udf5","mno");
                                intent.putExtra("trxn_address1", add1);
                                intent.putExtra("trxn_address2", add2);
                                intent.putExtra("trxn_city", city);
                                intent.putExtra("trxn_state", state);
                                intent.putExtra("trxn_country", country);
                                intent.putExtra("trxn_zipcode", pincode);
                                intent.putExtra("trxn_is_coupon_enabled",0);
                                intent.putExtra("trxn_salt", Helper.SALT_KEY);
                                intent.putExtra("unique_id", Helper.CUSTOMER_ID);
                                intent.putExtra("pay_mode", Helper.ENVIRONMENT);
                                startActivityForResult(intent, StaticDataModel.PWE_REQUEST_CODE);

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onlineDialog.dismiss();
                            }
                        });
                        onlineDialog = builder.create();
                        onlineDialog.show();
                    }
                }
            }
        });

        rbCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbOnline.setChecked(false);
            }
        });

        rbOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbCash.setChecked(false);
            }
        });
    }

    private void place(final String cusid, final String name, final String email, final String mobile, final String add1, final String add2, final String city, final String state, final String pincode, final String mode, final String cmobile) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, PLACE_URL,
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

                                    startActivity(new Intent(CheckoutActivity.this, PlaceActivity.class));
                                    FBToast.successToast(CheckoutActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);


                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    FBToast.errorToast(CheckoutActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                FBToast.errorToast(CheckoutActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            FBToast.errorToast(CheckoutActivity.this, e.getMessage(), FBToast.LENGTH_SHORT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
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
                        FBToast.errorToast(CheckoutActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("cusid", cusid);
                params.put("name", name);
                params.put("email", email);
                params.put("city", city);
                params.put("state", state);
                params.put("pincode", pincode);
                params.put("address", add1 + add2);
                params.put("mobileno", mobile);
                params.put("mode", mode);
                params.put("mobile", cmobile);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CheckoutActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data != null){
            String result = data.getStringExtra("result");
            String response = data.getStringExtra("payment_response");

            try {

                if (result.contains(StaticDataModel.TXN_SUCCESS_CODE)){
                    FBToast.successToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_TIMEOUT_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_BACKPRESSED_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_USERCANCELLED_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_ERROR_SERVER_ERROR_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_ERROR_TXN_NOT_ALLOWED_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_BANK_BACK_PRESSED_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_INVALID_INPUT_DATA_CODE)){
                    Toast.makeText(CheckoutActivity.this, result, Toast.LENGTH_LONG).show();
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_FAILED_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, Toast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_ERROR_NO_RETRY_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else if (result.contains(StaticDataModel.TXN_ERROR_RETRY_FAILED_CODE)){
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                    transaction(response);
                }else {
                    transaction(response);
                    FBToast.errorToast(CheckoutActivity.this, result, FBToast.LENGTH_LONG);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }else {
            FBToast.errorToast(CheckoutActivity.this, "Null", FBToast.LENGTH_LONG);
        }

    }

    private void transaction(String response) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject != null){

                String paytxntid = jsonObject.getString("txnid");
                String paybankissue = jsonObject.getString("issuing_bank");
                String paycardcat = jsonObject.getString("cardCategory");
                String payunmapstatus = jsonObject.getString("unmappedstatus");
                String paycardtype = jsonObject.getString("card_type");
                String payamout = jsonObject.getString("amount");
                String payfurl = jsonObject.getString("furl");
                String paysurl = jsonObject.getString("surl");
                String payproinfo = jsonObject.getString("productinfo");
                String payname = jsonObject.getString("firstname");
                String payemail = jsonObject.getString("email");
                String paymobile = jsonObject.getString("phone");
                String paystatus = jsonObject.getString("status");
                String paytime = jsonObject.getString("addedon");
                String paysource = jsonObject.getString("payment_source");
                String paypgtype = jsonObject.getString("PG_TYPE");
                String paybankrefno = jsonObject.getString("bank_ref_num");
                String paymerlogo = jsonObject.getString("merchant_logo");
                String paybankcode = jsonObject.getString("bankcode");
                String paykey = jsonObject.getString("key");
                String payerrormsg = jsonObject.getString("error_Message");
                String payerror = jsonObject.getString("error");
                String paycardname = jsonObject.getString("name_on_card");
                String paycardno = jsonObject.getString("cardnum");
                String payeaseid = jsonObject.getString("easepayid");
                String paynetamount = jsonObject.getString("net_amount_debit");
                String paycashback = jsonObject.getString("cash_back_percentage");
                String paydeduction = jsonObject.getString("deduction_percentage");
                String payudf1 = jsonObject.getString("udf1");
                String payudf2 = jsonObject.getString("udf2");
                String payudf3 = jsonObject.getString("udf3");
                String payudf4 = jsonObject.getString("udf4");
                String payudf5 = jsonObject.getString("udf5");
                String payudf6 = jsonObject.getString("udf6");
                String payudf7 = jsonObject.getString("udf7");
                String payudf8 = jsonObject.getString("udf8");
                String payudf9 = jsonObject.getString("udf9");
                String payudf10 = jsonObject.getString("udf10");
                String paymode = jsonObject.getString("mode");
                String payhash = jsonObject.getString("hash");
                String payflag = jsonObject.getString("flag");

                if (paystatus.equalsIgnoreCase("success")){

                    String cusid = Prefs.getString("uid", null);
                    String email = Prefs.getString("uemail", null);
                    String mobile = Prefs.getString("umobile", null);
                    String cname = tvName.getText().toString().trim();
                    String cmobile = tvMobile.getText().toString().trim();
                    String cadd1 = tvAdd1.getText().toString().trim();
                    String cadd2 = tvAdd2.getText().toString().trim();
                    String ccity = tvCity.getText().toString().trim();
                    String cstate = tvState.getText().toString().trim();
                    String cpincode = tvPincode.getText().toString().trim();
                    String mode = "Online";

                    place(cusid, cname, email, mobile, cadd1, cadd2, ccity, cstate, cpincode, mode, cmobile);

                    payment(paytxntid, paybankissue, paycardcat, payunmapstatus, paycardtype,
                            payamout, payproinfo, payname, payemail, paymobile, paystatus, paytime, paysource,
                            paypgtype, paybankrefno, paybankcode, payerrormsg, paycardname,
                            paycardno, payeaseid, paynetamount, paycashback, paydeduction, paymerlogo,
                            paykey, payerror, paymode, payhash);

                }else {

                    payment(paytxntid, paybankissue, paycardcat, payunmapstatus, paycardtype,
                            payamout, payproinfo, payname, payemail, paymobile, paystatus, paytime, paysource,
                            paypgtype, paybankrefno, paybankcode, payerrormsg, paycardname,
                            paycardno, payeaseid, paynetamount, paycashback, paydeduction, paymerlogo,
                            paykey, payerror, paymode, payhash);
                }


            }else {
                FBToast.errorToast(CheckoutActivity.this, "Null", FBToast.LENGTH_LONG);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void payment(final String paytxntid, final String paybankissue, final String paycardcat, final String payunmapstatus,
                         String paycardtype, final String payamout, String payproinfo, final String payname, final String payemail,
                         final String paymobile, final String paystatus, final String paytime, final String paysource, final String paypgtype,
                         final String paybankrefno, final String paybankcode, final String payerrormsg, final String paycardname,
                         final String paycardno, final String payeaseid, final String paynetamount, final String paycashback,
                         final String paydeduction, final String paymerlogo, final String paykey, String payerror, String paymode,
                         final String payhash) {

        progress.animate();
        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        StringRequest request = new StringRequest(Request.Method.POST, PAYMENT_URL,
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

                                    FBToast.successToast(CheckoutActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);

                                } else if (jsonObject.getString("status")
                                        .equalsIgnoreCase("failed")){
                                    progress.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    FBToast.errorToast(CheckoutActivity.this, jsonObject.getString("message"), FBToast.LENGTH_SHORT);
                                }
                            }else {
                                progress.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                FBToast.errorToast(CheckoutActivity.this, "Something went wrong", FBToast.LENGTH_SHORT);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            FBToast.errorToast(CheckoutActivity.this, e.getMessage(), FBToast.LENGTH_LONG);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
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
                        FBToast.errorToast(CheckoutActivity.this, message, FBToast.LENGTH_SHORT);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("status", paystatus);
                params.put("name_on_card", paycardname);
                params.put("bank_ref_num", paybankrefno);
                params.put("hash", payhash);
                params.put("firstname", payname);
                params.put("net_amount_debit", paynetamount);
                params.put("payment_source", paysource);
                params.put("error_Message", payerrormsg);
                params.put("issuing_bank", paybankissue);
                params.put("cardCategory", paycardcat);
                params.put("phone", paymobile);
                params.put("easepayid", payeaseid);
                params.put("cardnum", paycardno);
                params.put("key", paykey);
                params.put("unmappedstatus", payunmapstatus);
                params.put("PG_TYPE", paypgtype);
                params.put("addedon", paytime);
                params.put("cash_back_percentage", paycashback);
                params.put("merchant_logo", paymerlogo);
                params.put("txnid", paytxntid);
                params.put("amount", payamout);
                params.put("bankcode", paybankcode);
                params.put("deduction_percentage", paydeduction);
                params.put("email", payemail);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(CheckoutActivity.this);
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        queue.add(request);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CheckoutActivity.this, CartActivity.class));
        Bungee.swipeLeft(CheckoutActivity.this);
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
