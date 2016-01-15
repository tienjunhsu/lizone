package com.cquant.lizone.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cquant.lizone.R;
import com.cquant.lizone.activity.BaseActivity;
import com.cquant.lizone.activity.EditOptActivity;
import com.cquant.lizone.activity.KPointActivity;
import com.cquant.lizone.activity.MainActivity;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.LogTool;
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

	private MarketDataItem dataItem;
	private Context context;

    public OptItemView(Context context) {
        super(context);
        initView(context);
		context = context;//hsu
    }
    public OptItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
		context = context;//hsu
    }
    public OptItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
		context = context;//hsu
    }


    private void initView(Context context) {
        View.inflate(context, R.layout.opt_item_view, this);
        mTvName = (TextView)findViewById(R.id.tv_name);
        mTvPrice=(TextView)findViewById(R.id.tv_price);
        mTvAmp = (TextView)findViewById(R.id.tv_amp);

        red = context.getResources().getColor(R.color.red_two);
        green = context.getResources().getColor(R.color.green_two);
        white = context.getResources().getColor(R.color.white_two);

        setOnClickListener(mAddOptListener);//HSU
    }
    public void setMarketItem(MarketDataItem item) {
		dataItem = item;//hsu
        mTvName.setText(item.name);
        mTvPrice.setText(item.newprice+"");
        double amp = StrTool.sub(item.newprice, item.close);
        double amppercent ;
        if(item.close != 0) {
            amppercent = amp*100.0/item.close;
        }else {
            amppercent =0;
        }

        LogTool.d("setMarketItem:amp="+formatAmp(amp, amppercent));
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
	//begin add by hsu
	public void setItemName(String name) {
        mTvName.setText(name);
        mTvPrice.setText("0");
        mTvAmp.setText("0");
        mTvPrice.setTextColor(white);
        mTvAmp.setTextColor(white);

		//MarketDataItem item = new MarketDataItem(id,0,null,0,0,name,0,label,0,0,0);//hsu
		//dataItem = item;

    }
	//end add by hsu
    public void setAddSymbol() {
        LogTool.e("setAddSymbol");
        if(dataItem != null) {
            clearData();
        }

        mTvName.setText("");
        mTvPrice.setText("十");
        mTvPrice.setTextColor(getContext().getResources().getColor(R.color.blue_two));
        TextPaint tp = mTvPrice.getPaint();
        tp.setFakeBoldText(true);
        mTvAmp.setText("添加自选");
        mTvAmp.setTextColor(white);
        invalidate();//add by hsu,2015/12/21
        LogTool.e("setAddSymbol:symbol ="+mTvPrice.getText());
    }

    public void clearData() {
        dataItem = null;
    }

    public boolean isSymbol() {
        return dataItem == null;
    }

    private String formatAmp(double amp,double amppercent) {
        return amp +" "+String.format("%.2f",amppercent)+"%";
    }

    private OnClickListener mAddOptListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            LogTool.d(TAG + ",mAddOptListener:"+mTvPrice.getText());
			if(dataItem != null) {
                startKpointActivity();
			} else {
                startEditOptActivity();
			}
        }
    };
	private void startKpointActivity(){
        Intent intent = new Intent(getContext(), KPointActivity.class);
        Bundle mBundle = new Bundle();
       // mBundle.putSerializable("marketDataItem", item);
        mBundle.putParcelable("marketDataItem", dataItem);
        intent.putExtras(mBundle);
        getContext().startActivity(intent);
    }

	private void startEditOptActivity(){
		Intent intent = new Intent(getContext(),EditOptActivity.class);
		//intent.putExtras();
		//getContext().startActivity(intent);

        ((MainActivity)getContext()).startActivityForResult(intent, BaseActivity.EDIT_OPT_CODE);//hsu
	}
}
