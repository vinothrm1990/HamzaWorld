package com.app.hamzaworld.other;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(mailTo = "shadowwsvinothkumar@gmail.com")
public class HamzaWorld extends Application {

    public static HamzaWorld hamzaWorld;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        InternetAvailabilityChecker.init(this);
        hamzaWorld =this;
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners();
    }

    public static void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
}