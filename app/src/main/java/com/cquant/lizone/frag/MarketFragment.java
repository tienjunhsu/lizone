package com.cquant.lizone.frag;

import android.content.Intent;
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
import com.cquant.lizone.activity.KPointActivity;
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
import java.util.Iterator;
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

    private int red ;
    private int green;
    private int white;

    private HashMap expandedPositions = new HashMap();

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
        expandedPositions.put(0, 0);//hsu,2015/10/19,默认打开第一组
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.market_fragment, container, false);
        listView = (ExpandableListView)root.findViewById(R.id.market_list);
        listView.addHeaderView(getHeaderView());
        //listView.setGroupIndicator(getActivity().getResources().getDrawable(R.drawable.expand_list_indicator));
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

        //optView = (OptLayout)header.findViewById(R.id.opt_list);
        //optView.initView();

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
            mFixVarPrice01.setTextColor(red);
            mFixVarAmp01.setTextColor(red);
        } else if(amp < 0) {
            mFixVarPrice01.setTextColor(green);
            mFixVarAmp01.setTextColor(green);
        } else {
            mFixVarPrice01.setTextColor(white);
            mFixVarAmp01.setTextColor(white);
        }

        item = mFixHeaderData.get(1);
        mFixVarName02.setText(item.name);
        amp = StrTool.sub(item.newprice,item.close);
        amppercent = amp*100.0/item.close;
        mFixVarPrice02.setText(item.newprice+"");
        mFixVarAmp02.setText(formatAmp(amp,amppercent));

        if(amp > 0) {
            mFixVarPrice02.setTextColor(red);
            mFixVarAmp02.setTextColor(red);
        } else if(amp < 0) {
            mFixVarPrice02.setTextColor(green);
            mFixVarAmp02.setTextColor(green);
        } else {
            mFixVarPrice02.setTextColor(white);
            mFixVarAmp02.setTextColor(white);
        }

        item = mFixHeaderData.get(2);
        mFixVarName03.setText(item.name);
        amp = StrTool.sub(item.newprice,item.close);
        amppercent = amp*100.0/item.close;
        mFixVarPrice03.setText(item.newprice+"");
        mFixVarAmp03.setText(formatAmp(amp,amppercent));

        if(amp > 0) {
            mFixVarPrice03.setTextColor(red);
            mFixVarAmp03.setTextColor(red);
        } else if(amp < 0) {
            mFixVarPrice03.setTextColor(green);
            mFixVarAmp03.setTextColor(green);
        } else {
            mFixVarPrice03.setTextColor(white);
            mFixVarAmp03.setTextColor(white);
        }

    }
    private String formatAmp(double amp,double amppercent) {
        return amp +"  "+String.format("%.2f",amppercent)+"%";
    }
    @Override
    public void onStart() {
        super.onStart();;
        initView();
        initMarketData();
        getOptList();
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
        expandListView();
        LogTool.d(".......MarketFragment:onResume");
    }

    private void expandListView() {
        Iterator iterator = expandedPositions.keySet().iterator();
        while(iterator.hasNext()) {
            listView.expandGroup((int)iterator.next());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        disconnected();
        LogTool.d(".......MarketFragment:onPause");
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
        red = getActivity().getResources().getColor(R.color.red_two);
        green = getActivity().getResources().getColor(R.color.green_two);
        white = getActivity().getResources().getColor(R.color.white_two);
    }

    private void initView() {
        marketAdapter = new MarketAdapter();
        listView.setAdapter(marketAdapter);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                LogTool.d("onGroupExpand:" + i);
                expandedPositions.put(i, i);
            }
        });
        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                expandedPositions.remove(i);
            }
        });
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
        if(socket != null) {
            if(socket.connected()) {
                socket.disconnect();
                socket.close();
            }
            socket = null;
        }
        super.onDestroy();
    }

    private void startKpointActivity(MarketDataItem item){
        Intent intent = new Intent(getActivity(), KPointActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("marketDataItem",item);
        intent.putExtras(mBundle);
        startActivity(intent);
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
            //listView.expandGroup(groupPosition);//hsu
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
            final MarketDataItem item = mGroup.get(ex_label[groupPosition]).get(childPosition);
            mTvName.setText(item.name);
            mTvPrice.setText(item.newprice+"");
            double amp = StrTool.sub(item.newprice,item.close);
            double amppercent = amp*100.0/item.close;
            mTvAmp.setText(amp+"");
            mTvAmpRate.setText(String.format("%.2f",amppercent)+"%");

            if(amp > 0) {
                mTvPrice.setTextColor(red);
                mTvAmp.setTextColor(red);
                mTvAmpRate.setTextColor(red);
            } else if(amp < 0) {
                mTvPrice.setTextColor(green);
                mTvAmp.setTextColor(green);
                mTvAmpRate.setTextColor(green);
            } else {
                mTvPrice.setTextColor(white);
                mTvAmp.setTextColor(white);
                mTvAmpRate.setTextColor(white);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do something
                    startKpointActivity(item);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
