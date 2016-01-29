package com.cquant.lizone.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 2015/11/17.
 */
public class EditOptActivity extends BaseActivity {

    private static final String TAG = "EditOptActivity";

	private String[] ex_label = new String[] {"XHPEC","TJPME","SZPEC"};
    private String[] ex_name = new String[] {"新华商品现货合约交易中心","天津贵金属交易所","深圳石油化工交易所"};

	//begin add by hsu.2015/11/25
	private ArrayList<String> mOptId = new ArrayList<String>();
    private Map<String,MarketDataItem> mOptDataList = new HashMap<String,MarketDataItem>();
	//end add by hsu.2015/11/25

    private String selectedItem = "";

    private Toolbar toolbar;
    private ExpandableListView listView;

    private MarketAdapter marketAdapter;

	private Map<String,ArrayList<MarketDataItem>> mGroup = new HashMap<String,ArrayList<MarketDataItem>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_opt_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView =(ExpandableListView)findViewById(R.id.market_list);

        if(GlobalVar.sGroup != null){
            mGroup = GlobalVar.sGroup;
        }

        initToolBar();

        parseOptListFromCache();

        marketAdapter = new MarketAdapter();
        listView.setAdapter(marketAdapter);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("添加自选");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void updateToNet(int id) {
        if(!isNetAvailable()) {
            return;
        }
        Request request = new Request.Builder().url(Utils.BASE_URL + "AddOptional/id/" + id).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    LogTool.e("updateToNet:onResponse->msg=" + msg);
                    /*JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            //toast success
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
            }
        });
    }
    private void parseOptListFromCache() {

        mOptId.clear();
        mOptDataList.clear();

        String mOptStr = SharedPrefsUtil.getStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_OPT, null);
        LogTool.e("parseOptListFromCache:mOptStr =" + mOptStr);
        if(StrTool.areEmpty(mOptStr)) {
            return;
        }
        String items[] = mOptStr.split(",");
        for(int i=0;i<items.length;i++) {
            String attrs[] = items[i].split("#");

            //begin add by hsu.2015/12/23
            //对自选进行校订，防止无效的数据（因为服务器允许无效的数据上传上去）
            if(StrTool.areEmpty(attrs[1])||attrs[1].equals("null")) {
                continue;
            }
            //end add by hsu

            mOptId.add(attrs[0]);

            MarketDataItem dataItem = new MarketDataItem(attrs[0],attrs[2],attrs[1]);//attrs[0]是id,attrs[1]是label,attrs[2]是name
            mOptDataList.put(attrs[0],dataItem);
            LogTool.e("parseOptListFromCache:attrs[0] ="+attrs[0]+",attrs[1] ="+attrs[1]+",attrs[2]="+attrs[2]);
        }
    }

    private void saveOptStr() {
        String mOptStr = "";
        for(int i =0;i < mOptId.size();i++) {
            MarketDataItem item = mOptDataList.get(mOptId.get(i));
            LogTool.e("saveOptStr:length ="+mOptId.size()+",item="+item+",id="+mOptId.get(i));
            //begin mod by Tianjunhsu,2015/12/23
            //防止mOptStr以","开头
            /*if(i ==0) {
                mOptStr = item.id+"#"+item.label+"#"+item.name;
            } else {
                mOptStr =mOptStr+","+item.id+"#"+item.label+"#"+item.name;
            }*/
            if(mOptStr.trim().isEmpty()) {
                mOptStr = item.id+"#"+item.label+"#"+item.name;
            } else {
                mOptStr =mOptStr+","+item.id+"#"+item.label+"#"+item.name;
            }
            //end add by hsu
        }
        SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_OPT, mOptStr);
    }

    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }

    class MarketAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return 3;//hsu
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            int size = 0;
            try {
                size = mGroup.get(ex_label[groupPosition]).size();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroup.get(ex_label[groupPosition]);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            //测试一下没有数据的时候会不会出现空指针异常
            return mGroup.get(ex_label[groupPosition]).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_parent_item,parent,false);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.ex_name);
            tv.setText(ex_name[groupPosition]);
            listView.expandGroup(groupPosition);//hsu
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_opt_list_item,parent,false);
            }
            TextView mTvName = (TextView)convertView.findViewById(R.id.contract_name);
            TextView mTvLabel = (TextView)convertView.findViewById(R.id.contract_label);
            CheckBox mCheck = (CheckBox)convertView.findViewById(R.id.check_box);
            final MarketDataItem item = mGroup.get(ex_label[groupPosition]).get(childPosition);

            mTvName.setText(item.name);
            mTvLabel.setText(item.label);

            mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        //set as selected
                        if (!mOptId.contains(item.id + "")) {
                            if (mOptId.size() >= 5) {
                                compoundButton.setChecked(false);
                                popMsg("自选最多5个");
                            } else {
                                mOptId.add(item.id + "");
                                mOptDataList.put(item.id + "", item);
                                updateToNet(item.id);
                                saveOptStr();
                            }
                        }
                    } else {
                        //select cancle
                        if (mOptId.contains(item.id + "")) {
                            mOptId.remove(item.id + "");
                            mOptDataList.remove(item.id + "");
                            updateToNet(item.id);
                            popMsg("取消选择");
                            saveOptStr();
                        }
                    }
                }
            });

            LogTool.e("getChildView:item.id=" + item.id);
            if(mOptDataList.containsKey(item.id+"")) {
                LogTool.e("containsKey.......");
                mCheck.setChecked(true);
            } else {
                mCheck.setChecked(false);
			}

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
