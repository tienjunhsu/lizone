package com.cquant.lizone;

import android.app.Application;
import android.content.Context;

import com.cquant.lizone.tool.ACache;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by PC on 2015/8/24.
 */

public class LizoneApp extends Application {

    private RefWatcher refWatcher;

    private static LizoneApp app;
    private static ACache mACache;

    @Override
    public void onCreate() {
        super.onCreate();
        app= (LizoneApp) getApplicationContext();
        mACache = ACache.get(app,1024*1024*30,200);
        LeakCanary.install(this);
    }
    public static RefWatcher getRefWatcher(Context context) {
        LizoneApp application = (LizoneApp) context.getApplicationContext();
        return application.refWatcher;
    }
    public static LizoneApp getApp() {
        return app;
    }
    public static ACache getACache() {
        return mACache;
    }
    @Override
    public void onLowMemory() {
        mACache.clear();
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
