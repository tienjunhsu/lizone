package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import com.cquant.lizone.R;
import com.cquant.lizone.net.LoginWatcher;

/**
 * Created by PC on 2015/8/24.
 */
public  class BaseActivity extends AppCompatActivity {

    public void onXmlBtClick(View v) {

    }

    protected  void initToolBar(Toolbar toolbar) {
        if(toolbar == null) {
            return;
        }
        toolbar.setBackgroundColor(getColorPrimary());
        //toolbar.setTitleTextColor();
        setSupportActionBar(toolbar);

    }

    public int getColorPrimary() {
        TypedValue typeValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typeValue, true);
        return  typeValue.data;
    }

    protected  void initToolBar() {

    };
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
}
