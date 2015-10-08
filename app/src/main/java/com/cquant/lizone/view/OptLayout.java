package com.cquant.lizone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cquant.lizone.R;

/**
 * Created by PC on 2015/10/8.
 */
public class OptLayout  extends LinearLayout{

    private int wid;

    public OptLayout(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wid = wm.getDefaultDisplay().getWidth();
    }
    public OptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wid = wm.getDefaultDisplay().getWidth();
        //wid = 600;
    }
    public OptLayout(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs,defStyle);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wid = wm.getDefaultDisplay().getWidth();
    }
    public void initView() {
        int h = 80;
        int margin = 10;
        LayoutParams vlp = new LayoutParams(
                (wid-4*margin)/3,
                h);
        vlp.setMargins(margin,margin,0,margin);
        for(int i=0;i<3;i++) {
            TextView textView = new TextView(getContext());
            textView.setId(1729 + i);
            textView.setText("Text :" + i);
            //textView.setWidth(wid / 3);
            //textView.setHeight(h);
            textView.setLayoutParams(vlp);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.opt_bg);
            addView(textView);
        }
    }
}
