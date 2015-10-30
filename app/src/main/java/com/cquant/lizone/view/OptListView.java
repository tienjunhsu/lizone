package com.cquant.lizone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.cquant.lizone.R;
import com.cquant.lizone.tool.LogTool;

/**
 * Created by asus on 2015/10/26.
 */
public class OptListView extends LinearLayout {

    private static final String TAG = "OptListView";

    public OptListView(Context context) {
        super(context);
        initView(context);
    }
    public OptListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public OptListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        View.inflate(context, R.layout.opt_list_view, this);
    }
    public void setSize(int len) {
        LogTool.e(TAG+"setSize len="+len+",count ="+getChildCount()+",count2="+((LinearLayout)getChildAt(0)).getChildCount());
        LinearLayout child = (LinearLayout)getChildAt(0);
        if(len < 3) {
            child.getChildAt(1).setVisibility(GONE);
            LinearLayout layOne = (LinearLayout)child.getChildAt(0);
            for(int i=2;i>len;i--) {
                layOne.getChildAt(i).setVisibility(INVISIBLE);
            }
        } else {
            LinearLayout layTwo = (LinearLayout)child.getChildAt(1);
            for(int i=5;i>len;i--) {
                layTwo.getChildAt(i-3).setVisibility(INVISIBLE);
            }
        }

    }
}
