package com.cquant.lizone.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import com.cquant.lizone.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by PC on 2015/8/24.
 */
public class BaseFragActivity extends FragmentActivity {
    public void onXmlBtClick(View v) {

    }
    protected  void initToolBar(Toolbar toolbar) {
        if(toolbar == null) {
            return;
        }
        toolbar.setBackgroundColor(getColorPrimary());
        //toolbar.setTitleTextColor();
        //setActionBar(toolbar);

    }

    public int getColorPrimary() {
        TypedValue typeValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary,typeValue,true);
        return  typeValue.data;
    }

    protected  void initToolBar() {

    };
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

}
