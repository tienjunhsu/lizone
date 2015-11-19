package com.cquant.lizone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.cquant.lizone.R;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.LogTool;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/26.
 */
public class OptListView extends LinearLayout {

    private static final String TAG = "OptListView";

	private ArrayList<MarketDataItem> mOptList;//hsu
	private int optSize = 0;

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

    //begin add by hsu
	public void setOptName(ArrayList<String> names) {
		int len = 0;
	    if(names != null) {
            len = mOptList.size();
		}
        optSize = len;
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

		for(int i=0;i <= optSize;i++) {
            if(i==optSize ) {
                int k = i;
                if(i >=3 ) {
                    k = i % 3;
                    ((LinearLayout)child.getChildAt(0)).getChildAt(k).setOnClickListener(mAddOptListener);
                }
            } else if(i < 3) {
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(0)).getChildAt(i);
				item.setItemName(names.get(i));
			} else {
				int j = i %3;
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(1)).getChildAt(j);
				item.setItemName(names.get(i));
			}
		}
	  
	}

	public void setOptData(ArrayList<MarketDataItem> mOptList) {
		if(optSize <1) {
			return;
		}
        LinearLayout child = (LinearLayout)getChildAt(0);
        for(int i=0;i < mOptList.size();i++) {
            if(i < 3) {
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(0)).getChildAt(i);
				item.setMarketItem(mOptList.get(i));
			} else {
				int j = i %3;
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(1)).getChildAt(j);
				item.setMarketItem(mOptList.get(i));
			}
		}

	}
    private OnClickListener mAddOptListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            LogTool.d(TAG+",mAddOptListener");
        }
    };
	//end add by hsu
}
