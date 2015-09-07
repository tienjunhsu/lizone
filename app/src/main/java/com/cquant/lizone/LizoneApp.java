package com.cquant.lizone;

import android.app.Application;
import android.content.Context;

import com.cquant.lizone.tool.ACache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
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
        mACache = ACache.get(app,1024*1024*20,200);
        initImageLoader();
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
