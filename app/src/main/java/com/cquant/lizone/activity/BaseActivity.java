package com.cquant.lizone.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.cquant.lizone.R;
import com.cquant.lizone.net.LoginWatcher;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by PC on 2015/8/24.
 */
public class BaseActivity extends AppCompatActivity {

	public static final int FOLLOW_SETTING_CODE = 1112;
	public static final int EDIT_OPT_CODE = 1113;


    public void onXmlBtClick(View v) {

    }

    protected void initToolBar(Toolbar toolbar) {
        if (toolbar == null) {
            return;
        }
        toolbar.setBackgroundColor(getColorPrimary());
        //toolbar.setTitleTextColor();
        setSupportActionBar(toolbar);

    }

    public int getColorPrimary() {
        TypedValue typeValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typeValue, true);
        return typeValue.data;
    }

    protected void initToolBar() {

    }

    ;

    protected void popMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginWatcher.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginWatcher.unregister(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public boolean isNetAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if ((networkInfo != null) && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }
}
