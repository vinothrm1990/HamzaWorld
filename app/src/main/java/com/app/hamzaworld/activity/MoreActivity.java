package com.app.hamzaworld.activity;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hamzaworld.R;
import com.app.hamzaworld.other.CustomFont;
import com.app.hamzaworld.other.HamzaWorld;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import spencerstudios.com.bungeelib.Bungee;

public class MoreActivity extends AppCompatActivity implements InternetConnectivityListener {

    InternetAvailabilityChecker availabilityChecker;
    Menu menu;
    AlertDialog logoutDialog;
    BottomNavigationView navigation;
    LinearLayout orderLayout, bagLayout, supportLayout, privacyLayout, rateLayout, logoutLayout, trackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        availabilityChecker = InternetAvailabilityChecker.getInstance();
        availabilityChecker.addInternetConnectivityListener(this);

        TextView title = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(layoutParams);
        title.setText("MORE");
        title.setTextSize(23);
        title.setTextColor(Color.parseColor("#FFFFFF"));
        Typeface font = Typeface.createFromAsset(getAssets(), "share_bold.otf");
        title.setTypeface(font);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(title);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_more).setChecked(true);

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

        orderLayout = findViewById(R.id.more_order);
        bagLayout = findViewById(R.id.more_bag);
        supportLayout = findViewById(R.id.more_support);
        privacyLayout = findViewById(R.id.more_privacy);
        rateLayout = findViewById(R.id.more_rate);
        logoutLayout = findViewById(R.id.more_logout);
        trackLayout = findViewById(R.id.more_track);

        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Prefs.getBoolean("notLoggedIn", true)) {
                    FBToast.infoToast(MoreActivity.this, "Login or Register to Proceed", FBToast.LENGTH_SHORT);
                }else {
                    startActivity(new Intent(MoreActivity.this, OrderActivity.class));
                    Bungee.swipeRight(MoreActivity.this);
                }
            }
        });

        bagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Prefs.getBoolean("notLoggedIn", true)) {
                    FBToast.infoToast(MoreActivity.this, "Login or Register to Proceed", FBToast.LENGTH_SHORT);
                }else {
                    startActivity(new Intent(MoreActivity.this, BagActivity.class));
                    Bungee.swipeRight(MoreActivity.this);
                }
            }
        });

        supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoreActivity.this, SupportActivity.class));
                Bungee.swipeRight(MoreActivity.this);
            }
        });

        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoreActivity.this, PrivacyActivity.class));
                Bungee.swipeRight(MoreActivity.this);
            }
        });

        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    FBToast.errorToast(MoreActivity.this, "Unable to Find App in PlayStore", FBToast.LENGTH_SHORT);
                }

                /*startActivity(new Intent(MoreActivity.this, RateActivity.class));
                Bungee.swipeRight(MoreActivity.this);*/
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MoreActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you need to Logout ?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Prefs.clear();
                        startActivity(new Intent(MoreActivity.this, HomeActivity.class));
                        Bungee.fade(MoreActivity.this);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutDialog.dismiss();
                    }
                });
                logoutDialog = builder.create();
                logoutDialog.show();
            }
        });

        trackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoreActivity.this, TrackActivity.class));
                Bungee.swipeRight(MoreActivity.this);
            }
        });

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
                    startActivity(new Intent(MoreActivity.this, HomeActivity.class));
                    Bungee.fade(MoreActivity.this);
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(MoreActivity.this, ProfileActivity.class));
                    Bungee.fade(MoreActivity.this);
                    return true;
                case R.id.navigation_more:
                    startActivity(new Intent(MoreActivity.this, MoreActivity.class));
                    Bungee.fade(MoreActivity.this);
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
            startActivity(new Intent(MoreActivity.this, CartActivity.class));
            Bungee.swipeRight(MoreActivity.this);
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
