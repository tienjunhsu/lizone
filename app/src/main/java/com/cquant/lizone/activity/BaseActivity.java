package com.cquant.lizone.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import com.cquant.lizone.R;
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
        getTheme().resolveAttribute(R.attr.colorPrimary,typeValue,true);
        return  typeValue.data;
    }

    protected  void initToolBar() {

    };
}
