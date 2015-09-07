package com.cquant.lizone.net;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by asus on 2015/9/8.
 */
public class LoginWatcher {
    private static int referNum = 0;

    private static  Timer timer_=null;
    private  static  String user_id;
    private static  String pass_word;

    private static WebHelper mWebhelper = null;

    public static void  register() {
        referNum = referNum +1;
        if(mWebhelper == null) {
            mWebhelper = new WebHelper(LizoneApp.getApp());
        }
        if(referNum ==1) {
            startLoginObserver();
        }
    }
    public static  void unregister() {
        referNum = referNum - 1;
        if(referNum <=0) {
            stopLoginObserver();
        }
    }
    private static void startLoginObserver() {
        timer_=new Timer();
        timer_.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 5*60*1000,5*60*1000);
    }
    private  static  void stopLoginObserver() {
        if(timer_ != null) {
            timer_.cancel();
        }
        timer_ = null;
    }
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    startLogin();
                    break;
            }
        }
    };

    private static void startLogin() {
        if(user_id == null) {
            user_id = SharedPrefsUtil.getStringValue(LizoneApp.getApp(),SharedPrefsUtil.PREFS_USER_ID,null);
        }
        if(pass_word == null) {
            pass_word = SharedPrefsUtil.getStringValue(LizoneApp.getApp(),SharedPrefsUtil.PREFS_PASS,null);
        }
        if((user_id == null)||(pass_word == null)) {
            return;
        }
        String paras = "name="+user_id+"&pwd="+pass_word;
        mWebhelper.doLoadPost(Utils.BASE_URL + Utils.LOGIN_ADDR, paras, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {

            }
        });
    }
}
