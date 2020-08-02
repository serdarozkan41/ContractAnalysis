package com.teesteknoloji.contractanalysis;

import android.app.Application;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.pixplicity.easyprefs.library.Prefs;
import com.teesteknoloji.contractanalysis.services.ApiManager;

public class MainApplication  extends Application {

    public static ApiManager apiManager;

    @Override
    public void onCreate() {
        super.onCreate();
        apiManager = ApiManager.getInstance();
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        AppCenter.start(this, "0eaa9f97-df37-4ec2-b4e4-37e6ecce16f9",
                Analytics.class, Crashes.class);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
