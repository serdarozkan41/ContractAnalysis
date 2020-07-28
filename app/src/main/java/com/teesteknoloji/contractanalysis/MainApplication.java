package com.teesteknoloji.contractanalysis;

import android.app.Application;
import android.content.ContextWrapper;
import android.content.res.Configuration;
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
