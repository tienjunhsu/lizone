package com.cquant.lizone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;

import org.json.JSONObject;

/**
 * Created by asus on 2015/9/5.
 */
public class UpdateConfigService extends Service {

    private static final String TAG = "UpdateConfigService";

    private WebHelper mWebhelper = null;
    private String promotions_url = Utils.BASE_URL + Utils.PROMOTIONS_ADDR;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWebhelper = new WebHelper(this);

        if(isNetWorkAvailable()) {
            getPromotions();
        } else {
            stopSelf();
        }
    }

    private void getPromotions() {
        mWebhelper.doLoadGet(promotions_url, null, new WebHelper.OnWebFinished() {

                    @Override
                    public void onWebFinished(boolean success, String msg) {
                        if(success) {
                            JSONObject response = JsnTool.getObject(msg);
                            if((response != null)&&(JsnTool.getInt(response,"status")==1))  {
                                savePromotions(msg);
                            }
                        }
                        stopSelf();
                    }
                });
    }

    private void savePromotions(String response) {
        SharedPrefsUtil.putStringValue(this,SharedPrefsUtil.PREFS_PROMOTIONS,response);
    }

    @Override
    public void onDestroy() {

    }
    private boolean isNetWorkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if ((networkInfo != null) && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }
}
