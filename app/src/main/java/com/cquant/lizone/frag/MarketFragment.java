package com.cquant.lizone.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.OptLayout;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by asus on 2015/8/30.
 */
public class MarketFragment extends BaseFragment {

    private static final int GROUP_COUNT = 3;
    private String[] ex_label = new String[] {"XHPEC","TJPME","SZPEC"};
    private String[] ex_name = new String[] {"新华商品现货合约交易中心","天津贵金属交易所","深圳石油化工交易所"};

    private Socket socket;
    private ACache mCache;
    private String mFileName;

    private String url = "http://1-yj.com:3000";
    private ExpandableListView listView;
    private MarketAdapter marketAdapter;

    private ArrayList<MarketDataItem> mDataList;

    //header view
    private TextView mFixVarName01;
    private TextView mFixVarPrice01;
    private TextView mFixVarAmp01;

    private TextView mFixVarName02;
    private TextView mFixVarPrice02;
    private TextView mFixVarAmp02;

    private TextView mFixVarName03;
    private TextView mFixVarPrice03;
    private TextView mFixVarAmp03;

    private ArrayList<MarketDataItem> mFixHeaderData;
    //
    private WebHelper mWebhelper;

    private OptLayout optView;


    private Map<String,ArrayList<MarketDataItem>> mGroup = new HashMap<String,ArrayList<MarketDataItem>>();

    private void groupData() {
        if(mDataList == null) {
            return;
        }
        mGroup.clear();
        for(MarketDataItem item:mDataList) {
            if(mGroup.containsKey(item.excode)) {
                ArrayList<MarketDataItem> list = mGroup.get(item.excode);
                list.add(item);
            } else {
                ArrayList list = new ArrayList<MarketDataItem>();
                list.add(item);
                mGroup.put(item.excode,list);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileName = Md5FileNameGenerator.generate(url);
        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("TianjunXu","connect:URISyntaxException "+e.getMessage());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.market_fragment, container, false);
        listView = (ExpandableListView)root.findViewById(R.id.market_list);
        listView.addHeaderView(getHeaderView());
        //listView.setGroupIndicator(nn);//箭头图标
        return root;
    }
    private View getHeaderView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View header = inflater.inflate(R.layout.market_header_view,null);
        mFixVarName01 = (TextView)header.findViewById(R.id.name_xau);
        mFixVarName02 = (TextView)header.findViewById(R.id.name_usd);
        mFixVarName03 = (TextView)header.findViewById(R.id.name_conc);

        mFixVarPrice01 = (TextView)header.findViewById(R.id.price_xau);
        mFixVarPrice02 = (TextView)header.findViewById(R.id.price_usd);
        mFixVarPrice03 = (TextView)header.findViewById(R.id.price_conc);

        mFixVarAmp01 = (TextView)header.findViewById(R.id.amp_xau);
        mFixVarAmp02 = (TextView)header.findViewById(R.id.amp_usd);
        mFixVarAmp03 = (TextView)header.findViewById(R.id.amp_conc);

        optView = (OptLayout)header.findViewById(R.id.opt_list);
        optView.initView();

        return header;
    }
    private void refreshFixHeaderView() {
        mFixHeaderData = mGroup.get("ZHISHU");
        if((mFixHeaderData == null)||mFixHeaderData.size() < 3) {
            return;
        }
        double amp=0;
        double amppercent = 0.00;
        MarketDataItem item;
        item = mFixHeaderData.get(0);
        mFixVarName01.setText(item.name);
        amp = StrTool.sub(item.newprice,item.close);
        amppercent = amp*100.0/item.close;
        mFixVarPrice01.setText(item.newprice+"");
        mFixVarAmp01.setText(formatAmp(amp,amppercent));

        if(amp > 0) {
            mFixVarPrice01.setTextColor(getResources().getColor(R.color.red_two));
            mFixVarAmp01.setTextColor(getResources().getColor(R.color.red_two));
        } else if(amp < 0) {
            mFixVarPrice01.setTextColor(getResources().getColor(R.color.green_two));
            mFixVarAmp01.setTextColor(getResources().getColor(R.color.green_two));
        } else {
            mFixVarPrice01.setTextColor(getResources().getColor(R.color.white_two));
            mFixVarAmp01.setTextColor(getResources().getColor(R.color.white_two));
        }

        item = mFixHeaderData.get(1);
        mFixVarName02.setText(item.name);
        amp = StrTool.sub(item.newprice,item.close);
        amppercent = amp*100.0/item.close;
        mFixVarPrice02.setText(item.newprice+"");
        mFixVarAmp02.setText(formatAmp(amp,amppercent));

        if(amp > 0) {
            mFixVarPrice02.setTextColor(getResources().getColor(R.color.red_two));
            mFixVarAmp02.setTextColor(getResources().getColor(R.color.red_two));
        } else if(amp < 0) {
            mFixVarPrice02.setTextColor(getResources().getColor(R.color.green_two));
            mFixVarAmp02.setTextColor(getResources().getColor(R.color.green_two));
        } else {
            mFixVarPrice02.setTextColor(getResources().getColor(R.color.white_two));
            mFixVarAmp02.setTextColor(getResources().getColor(R.color.white_two));
        }

        item = mFixHeaderData.get(2);
        mFixVarName03.setText(item.name);
        amp = StrTool.sub(item.newprice,item.close);
        amppercent = amp*100.0/item.close;
        mFixVarPrice03.setText(item.newprice+"");
        mFixVarAmp03.setText(formatAmp(amp,amppercent));

        if(amp > 0) {
            mFixVarPrice03.setTextColor(getResources().getColor(R.color.red_two));
            mFixVarAmp03.setTextColor(getResources().getColor(R.color.red_two));
        } else if(amp < 0) {
            mFixVarPrice03.setTextColor(getResources().getColor(R.color.green_two));
            mFixVarAmp03.setTextColor(getResources().getColor(R.color.green_two));
        } else {
            mFixVarPrice03.setTextColor(getResources().getColor(R.color.white_two));
            mFixVarAmp03.setTextColor(getResources().getColor(R.color.white_two));
        }

    }
    private String formatAmp(double amp,double amppercent) {
        return amp +"  "+String.format("%.2f",amppercent)+"%";
    }
    @Override
    public void onStart() {
        super.onStart();;
    }
    public void connect() {
        if(socket == null) {
            return;
        }
        if(socket.connected()) {
            return;
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("TianjunXu", "connect:EVENT_CONNECT");
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("TianjunXu","connect:EVENT_DISCONNECT");
            }

        }).on("notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Message msg = new Message();
                Message msg = mHanler.obtainMessage();
                msg.what = 2;
                msg.obj = args[0];
                msg.sendToTarget();
                /*JSONObject data = (JSONObject)args[0];
                if(data != null) {
                    parseMarketData(data);
                }*/
                //Log.d("TianjunXu", "connect:notification-->data="+data);
            }
        });;
        socket.connect();

        //test
       /* WebHelper mWebhelper = new WebHelper(getActivity());
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                Log.d("TianjunXu", " connect:success = " + success + ",msg =" + msg);
            }
        });*/
        //test
    }

    public void disconnected() {
        if((socket != null)&&socket.connected()) {
            socket.disconnect();
        }

    }
    private Handler mHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            } else if (msg.what == 2) {
                 JSONObject data = (JSONObject)msg.obj;
                if(data != null) {
                    parseMarketData(data);
                }

            }else if(msg.what==3){
                connect();
            }
            super.handleMessage(msg);
        }

    };
    private void getOptList() {
        String optUrl = Utils.BASE_URL+"optlist/";
        mWebhelper.doLoadGet(optUrl, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.d("getOptList:success = " + success + ",msg =" + msg);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        connect();
    }
    @Override
    public void onPause() {
        super.onPause();
        disconnected();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCache = LizoneApp.getACache();
        mWebhelper = new WebHelper(getActivity());
        initView();
        initMarketData();
        getOptList();
    }

    private void initView() {
        marketAdapter = new MarketAdapter();
        listView.setAdapter(marketAdapter);
    }

    private void initMarketData() {
        JSONObject obj = mCache.getAsJSONObject(mFileName);

        if(obj != null) {
            parseMarketData(obj);
        }
    }

    private void parseMarketData(JSONObject obj) {
        mDataList = MarketDataItem.getItemList(obj);
        groupData();
        marketAdapter.notifyDataSetChanged();
        refreshFixHeaderView();
        mCache.put(mFileName,obj);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MarketAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return GROUP_COUNT;
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
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_child_item,parent,false);
            }
            TextView mTvName = (TextView)convertView.findViewById(R.id.contract_name);
            TextView mTvPrice = (TextView)convertView.findViewById(R.id.contract_price);
            TextView mTvAmp = (TextView)convertView.findViewById(R.id.contract_amp);
            TextView mTvAmpRate = (TextView)convertView.findViewById(R.id.contract_amp_rate);
            MarketDataItem item = mGroup.get(ex_label[groupPosition]).get(childPosition);
            mTvName.setText(item.name);
            mTvPrice.setText(item.newprice+"");
            double amp = StrTool.sub(item.newprice,item.close);
            double amppercent = amp*100.0/item.close;
            mTvAmp.setText(amp+"");
            mTvAmpRate.setText(String.format("%.2f",amppercent)+"%");

            if(amp > 0) {
                mTvPrice.setTextColor(getResources().getColor(R.color.red_two));
                mTvAmp.setTextColor(getResources().getColor(R.color.red_two));
                mTvAmpRate.setTextColor(getResources().getColor(R.color.red_two));
            } else if(amp < 0) {
                mTvPrice.setTextColor(getResources().getColor(R.color.green_two));
                mTvAmp.setTextColor(getResources().getColor(R.color.green_two));
                mTvAmpRate.setTextColor(getResources().getColor(R.color.green_two));
            } else {
                mTvPrice.setTextColor(getResources().getColor(R.color.white_two));
                mTvAmp.setTextColor(getResources().getColor(R.color.white_two));
                mTvAmpRate.setTextColor(getResources().getColor(R.color.white_two));
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
