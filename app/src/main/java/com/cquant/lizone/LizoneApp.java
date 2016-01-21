package com.cquant.lizone;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by PC on 2015/8/24.
 */

public class LizoneApp extends Application {

    //private RefWatcher refWatcher;

    private static LizoneApp app;
    private static ACache mACache;

    public static boolean mCanLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        app= (LizoneApp) getApplicationContext();
        mACache = ACache.get(app,1024*1024*20,200);
        initImageLoader();
        //refWatcher = LeakCanary.install(this);
        checkLogin();
    }
    private  void checkLogin(){
        String name = SharedPrefsUtil.getStringValue(this,SharedPrefsUtil.PREFS_ACCOUNT,null);
        String pass = SharedPrefsUtil.getStringValue(this,SharedPrefsUtil.PREFS_PASS,null);
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(pass)) {
            mCanLogin = false;
        } else {
            mCanLogin = true;
        }
    }
    /*public static RefWatcher getRefWatcher(Context context) {
        LizoneApp application = (LizoneApp) context.getApplicationContext();
        return application.refWatcher;
    }*/
    public static LizoneApp getApp() {
        return app;
    }
    public static ACache getACache() {
        return mACache;
    }
    @Override
    public void onLowMemory() {
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        mACache.clear();
        super.onLowMemory();
    }
    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_default_adimage)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCacheSize(1024*1024*20)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
