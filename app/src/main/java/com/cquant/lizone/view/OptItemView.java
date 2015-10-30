package com.cquant.lizone.view;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cquant.lizone.R;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.StrTool;

/**
 * Created by asus on 2015/10/26.
 */
public class OptItemView extends LinearLayout {
    private static final String TAG = "OptItemView";

    private TextView mTvName;
    private TextView mTvPrice;
    private TextView mTvAmp;

    private int red;
    private int green;
    private int white;

    public OptItemView(Context context) {
        super(context);
        initView(context);
    }
    public OptItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public OptItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        View.inflate(context, R.layout.opt_item_view, this);
        mTvName = (TextView)findViewById(R.id.tv_name);
        mTvPrice=(TextView)findViewById(R.id.tv_price);
        mTvAmp = (TextView)findViewById(R.id.tv_amp);

        red = context.getResources().getColor(R.color.red_two);
        green = context.getResources().getColor(R.color.green_two);
        white = context.getResources().getColor(R.color.white_two);
    }
    public void setMarketItem(MarketDataItem item) {
        mTvName.setText(item.name);
        mTvPrice.setText(item.newprice+"");
        double amp = StrTool.sub(item.newprice, item.close);
        double amppercent = amp*100.0/item.close;
        mTvAmp.setText(formatAmp(amp, amppercent));
        if(amp > 0) {
            mTvPrice.setTextColor(red);
            mTvAmp.setTextColor(red);
        } else if(amp <0) {
            mTvPrice.setTextColor(green);
            mTvAmp.setTextColor(green);
        } else {
            mTvPrice.setTextColor(white);
            mTvAmp.setTextColor(white);
        }

    }
    public void setAddSymbol() {
        mTvName.setText("");
        mTvPrice.setText("十");
        mTvPrice.setTextColor(getContext().getResources().getColor(R.color.blue_two));
        TextPaint tp = mTvPrice.getPaint();
        tp.setFakeBoldText(true);
        mTvAmp.setText("添加自选");
        mTvAmp.setTextColor(white);
    }
    private String formatAmp(double amp,double amppercent) {
        return amp +"  "+String.format("%.2f",amppercent)+"%";
    }
}