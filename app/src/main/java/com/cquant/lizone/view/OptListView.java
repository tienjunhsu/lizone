package com.cquant.lizone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.cquant.lizone.R;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.LogTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2015/10/26.
 */
public class OptListView extends LinearLayout {

    private static final String TAG = "OptListView";

	//private ArrayList<MarketDataItem> mOptList;//hsu
	private Map<String,MarketDataItem> mOptList = new HashMap<String,MarketDataItem>();
    private ArrayList<String> mOptId = new ArrayList<String>();
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
        /*LinearLayout child = (LinearLayout)getChildAt(0);
        for(int i=0;i<=1;i++) {
            LinearLayout ly_child =(LinearLayout) child.getChildAt(i);
            for(int j=0;j<=2;j++) {
                OptItemView item = (OptItemView)ly_child.getChildAt(j);
                item.setOnClickListener(mAddOptListener);
            }
        }*/
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
            if(layOne.getChildAt(len).getVisibility() == INVISIBLE) {
                layOne.getChildAt(len).setVisibility(VISIBLE);
            }
        } else {
            LinearLayout layTwo = (LinearLayout)child.getChildAt(1);
            for(int i=5;i>len;i--) {
                layTwo.getChildAt(i-3).setVisibility(INVISIBLE);
            }
            if(layTwo.getChildAt(len-3).getVisibility() == INVISIBLE){
                layTwo.getChildAt(len-3).setVisibility(VISIBLE);
            }
            if(layTwo.getVisibility() == GONE) {
                layTwo.setVisibility(VISIBLE);
            }
        }

    }

    //begin add by hsu
	public void setOptInitData(ArrayList<String> ids,Map<String,MarketDataItem> list) {
		int len = 0;
	    if(list != null) {
            len = list.size();
			mOptId = ids;
			mOptList = list;
		}
        optSize = len;
        LogTool.e("setOptInitData,len ="+len);
        LinearLayout child = (LinearLayout)getChildAt(0);
	    if(len < 3) {
           child.getChildAt(1).setVisibility(GONE);
           LinearLayout layOne = (LinearLayout)child.getChildAt(0);
           for(int i=2;i>len;i--) {
               layOne.getChildAt(i).setVisibility(INVISIBLE);
           }
        } else {
            LinearLayout layTwo = (LinearLayout)child.getChildAt(1);
            //begin add by hsu,2015/12/21
            if(layTwo.getVisibility() == GONE) {
                layTwo.setVisibility(VISIBLE);
            }
            //end add by hsu
            for(int i=5;i>len;i--) {
                layTwo.getChildAt(i-3).setVisibility(INVISIBLE);
            }
        }

		for(int i=0;i <= optSize;i++) {
            LogTool.e("111: i="+i+",optSize ="+optSize);
            if(i==optSize ) {
                LogTool.e("222: i="+i+",optSize ="+optSize);
                int k = i;
                if(i >=3 ) {
                    k = i % 3;
                    OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(1)).getChildAt(k);
					item.setAddSymbol();
                    //begin add by hsu,2015/12/21
                    if(item.getVisibility() != VISIBLE) {
                        item.setVisibility(VISIBLE);
                    }
                    //end add by hsu
                } else {
                    OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(0)).getChildAt(k);
					item.setAddSymbol();

                    //begin add by hsu,2015/12/21
                    if(item.getVisibility() != VISIBLE) {
                        item.setVisibility(VISIBLE);
                    }
                    //end add by hsu
				}
            } else if(i < 3) {
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(0)).getChildAt(i);
				//item.setItemName(names.get(i));
				item.setMarketItem(list.get(mOptId.get(i)));
                //begin add by hsu,2015/12/21
                if(item.getVisibility() != VISIBLE) {
                    item.setVisibility(VISIBLE);
                }
                //end add by hsu
			} else {
				int j = i %3;
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(1)).getChildAt(j);
				//item.setItemName(names.get(i));
				item.setMarketItem(list.get(mOptId.get(i)));
                //begin add by hsu,2015/12/21
                if(item.getVisibility() != VISIBLE) {
                    item.setVisibility(VISIBLE);
                }
                //end add by hsu
			}
		}
	  
	}

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
                    OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(1)).getChildAt(k);
					item.setAddSymbol();
                } else {
                    OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(0)).getChildAt(k);
					item.setAddSymbol();
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

	public void setOptData(Map<String,MarketDataItem> list) {
		if(optSize <1) {
			return;
		}
        LogTool.e("setOptData,optSize ="+optSize);
        LinearLayout child = (LinearLayout)getChildAt(0);
        for(int i=0;i < mOptList.size();i++) {
            if(i < 3) {
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(0)).getChildAt(i);
                LogTool.e("setOptData,isSymbol ="+item.isSymbol());
				item.setMarketItem(list.get(mOptId.get(i)));
			} else {
				int j = i %3;
                OptItemView item = (OptItemView)((LinearLayout)child.getChildAt(1)).getChildAt(j);
				item.setMarketItem(list.get(mOptId.get(i)));
			}
		}

	}
	//end add by hsu
}
