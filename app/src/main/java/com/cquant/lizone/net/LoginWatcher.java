package com.cquant.lizone.net;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.bean.AccountItem;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by asus on 2015/9/8.
 */
public class LoginWatcher {
    private static int referNum = 0;

    private static  Timer timer_=null;
    private  static  String user_id = "18682169419";
    private static  String pass_word = "nmyzdg21254";

    private static WebHelper mWebhelper = null;

    public static void  register(Activity activity) {
        Log.d("TianjunXu", " register...."+activity.getComponentName());
        referNum++;
        if(mWebhelper == null) {
            mWebhelper = new WebHelper(LizoneApp.getApp());
        }
        if(referNum ==1) {
            startLoginObserver();
        }
    }
    public static  void unregister(Activity activity) {
        Log.d("TianjunXu", " unregister...."+activity.getComponentName());
        referNum--;
        if(referNum <=0) {
            stopLoginObserver();
        }
    }
    private static void startLoginObserver() {
        Log.d("TianjunXu", " startLoginObserver....");
        timer_=new Timer();
        timer_.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("TianjunXu", " schedule...");
                mHandler.sendEmptyMessage(0);
            }
        }, 0,5*60*1000);
    }
    private  static  void stopLoginObserver() {
        Log.d("TianjunXu", " stopLoginObserver....");
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

    public  static void setUser(String user_id,String pass,boolean isTrue) {
        user_id = user_id;
        pass_word = pass;
        if(isTrue) {
            saveAccount();
        }
    }
    public static void startLogin() {
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
                if(success) {
                    saveAccount();
                    parseAccountInf(msg);
                }
            }
        });
    }

    private static void saveAccount() {
        if(!LizoneApp.mCanLogin ) {
            LizoneApp.mCanLogin = true;
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_USER_ID, user_id);
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_PASS, pass_word);
        }
    }
    private static void parseAccountInf(String msg) {
        JSONObject json = JsnTool.getObject(msg);
        if((json != null)&&(JsnTool.getInt(json,"status")==1)) {
            GlobalVar.sAccountInf = AccountItem.getItem(json);
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(),SharedPrefsUtil.PREFS_ACCOUNT,msg);
        } else {
            parseAccountFromCache();
        }
    }
    private static void parseAccountFromCache() {
        String str = SharedPrefsUtil.getStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_ACCOUNT, null);
        if(str != null) {
            GlobalVar.sAccountInf = AccountItem.getItem(str);
        }
    }
}
