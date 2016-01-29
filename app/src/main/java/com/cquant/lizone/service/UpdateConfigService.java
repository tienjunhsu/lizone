package com.cquant.lizone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by asus on 2015/9/5.
 */
public class UpdateConfigService extends Service {

    private static final String TAG = "UpdateConfigService";

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

        if(isNetWorkAvailable()) {
            getPromotions();
        } else {
            stopSelf();
        }
    }

    private static final int MSG_PARSE_DATA = 21;
    private static final int MSG_STOP_SELF = 22;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_PARSE_DATA:{
                    savePromotions((String) msg.obj);
                    break;
                }
                case  MSG_STOP_SELF:{
                    stopSelf();
                    break;
                }
                default:stopSelf();
            }
        }
    };
    private void getPromotions() {
        Request request = new Request.Builder().url(promotions_url).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Message message = mHandler.obtainMessage();
                message.what = MSG_STOP_SELF;
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    if ((obj != null) && (JsnTool.getInt(obj, "status") == 1)) {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_PARSE_DATA;
                        message.obj = msg;
                        message.sendToTarget();
                    } else {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_STOP_SELF;
                        message.sendToTarget();
                    }
                } else {
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_STOP_SELF;
                    message.sendToTarget();
                }
            }
        });
    }

    private void savePromotions(String response) {
        SharedPrefsUtil.putStringValue(this,SharedPrefsUtil.PREFS_PROMOTIONS,response);
    }

    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
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
