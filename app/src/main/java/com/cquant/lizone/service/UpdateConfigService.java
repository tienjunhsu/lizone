package com.cquant.lizone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;

import org.json.JSONObject;

/**
 * Created by asus on 2015/9/5.
 */
public class UpdateConfigService extends Service {

    private static final String TAG = "UpdateConfigService";

    private RequestQueue mVolleyQueue;
    private JsonObjectRequest mPromotionRequest;
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

        mVolleyQueue = Volley.newRequestQueue(this);

        if(isNetWorkAvailable()) {
            getPromotions();
        } else {
            stopSelf();
        }
    }

    private void getPromotions() {
        mPromotionRequest = new JsonObjectRequest(Request.Method.GET, promotions_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(JsnTool.getInt(response,"status")==1) {
                   savePromotions(response);
                }
                stopSelf();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
                // For AuthFailure, you can re login with user credentials.
                // For ClientError, 400 & 401, Errors happening on client side when sending api request.
                // In this case you can check how client is forming the api and debug accordingly.
                // For ServerError 5xx, you can do retry or handle accordingly.
                Log.e(TAG, "error " + error.getMessage(), error);
                stopSelf();
            }
        });
        mPromotionRequest.setTag(TAG);
        mPromotionRequest.setRetryPolicy(new DefaultRetryPolicy(2500, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mVolleyQueue.add( mPromotionRequest);

    }

    private void savePromotions(JSONObject response) {
        SharedPrefsUtil.putStringValue(this,SharedPrefsUtil.PREFS_PROMOTIONS,response.toString());
    }

    @Override
    public void onDestroy() {
        mVolleyQueue.cancelAll(TAG);
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
