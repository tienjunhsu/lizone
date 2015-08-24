package com.cquant.lizone;

import android.app.Application;
import android.content.Context;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by PC on 2015/8/24.
 */

public class LizoneApp extends Application {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
    public static RefWatcher getRefWatcher(Context context) {
        LizoneApp application = (LizoneApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
