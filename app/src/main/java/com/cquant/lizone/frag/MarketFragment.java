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
import com.cquant.lizone.activity.BaseActivity;
import com.cquant.lizone.activity.KPointActivity;
import com.cquant.lizone.activity.MainActivity;
import com.cquant.lizone.bean.MarketDataItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.OptListView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
public class MarketFragment extends BaseFragment implements MainActivity.OnActivityResultObserver {

    private static final String TAG = "MarketFragment";

    private static final int GROUP_COUNT = 3;

     //mod by hsu,api 2.0,2016/01/15
    // private String[] ex_label = new String[] {"XHPEC","TJPME","SZPEC"};
    //private String[] ex_name = new String[] {"新华商品现货合约交易中心","天津贵金属交易所","深圳石油化工交易所"};
    //private String url = "http://1-yj.com:3000";
     private String[] ex_label = new String[] {"DJS","TJPME","XHPEC"};
     private String[] ex_name = new String[] {"天津电子材料与产品交易所","天津贵金属交易所","新华商品现货合约交易中心"};
     private String url = "http://q.lizone.net:3000";
    //end mod by hsu

    private Socket socket;
    private ACache mCache;
    private String mFileName;

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

    private OptListView mOptListView;
    private Map<String,MarketDataItem> mOptDataList = new HashMap<String,MarketDataItem>();
    private ArrayList<String> mOptId = new ArrayList<String>();
	private ArrayList<String> mOptNames = new ArrayList<String>();

	private String mOptStr;//保存在本地的自选字符串,单个是id#label#name,形式如"1#XAGUSD#现货白银,2#AG1000#现货白银1K"

    //

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
            if(mOptDataList.containsKey(item.id+"")) {//自选合约
                mOptDataList.put(item.id+"",item);
            }
        }
        //begin add by hsu
        //EditOptActivity Get mGrounp by GlobalVar
        if(GlobalVar.sGroup  == null) {
            GlobalVar.sGroup = mGroup;
        }
        //end add by hsu
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
        LogTool.v("MarketFragment:onCreate");//hsu
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.market_fragment, container, false);
        listView = (ExpandableListView)root.findViewById(R.id.market_list);
        listView.addHeaderView(getHeaderView());
        //listView.setGroupIndicator(getActivity().getResources().getDrawable(R.drawable.expand_list_indicator));
        LogTool.e("MarketFragment:onCreateView,size ="+mOptId.size());//hsu
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

        mOptListView = (OptListView)header.findViewById(R.id.opt_list);

        LogTool.e("MarketFragment:getHeaderView");//hsu
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
        LogTool.v("MarketFragment:onStart");//hsu
        initView();
        initMarketData();
        getOptList();
        //setActivityResultObserver();
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
    }

    public void disconnected() {
        if((socket != null)&&socket.connected()) {
            socket.off();//add by hsu,2016/01/15
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
            } else if(msg.what ==MSG_PARSE_OPT_LIST) {
                parseOptList((String)msg.obj);
                resizeOptListView();
            }else if(msg.what ==MSG_PARSE_OPT_FROM_CACHE) {
                parseOptListFromCache();
                resizeOptListView();
            }
            super.handleMessage(msg);
        }

    };

    private static final int MSG_PARSE_OPT_LIST = 30;
    private static final int MSG_PARSE_OPT_FROM_CACHE = 31;
    private void getOptList() {
        String optUrl = Utils.BASE_URL+"optlist/";
		if(((BaseActivity)getActivity()).isNetAvailable()) {
            Request request = new Request.Builder().url(optUrl).tag(TAG).build();
            LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Message message = mHanler.obtainMessage();
                    message.what = MSG_PARSE_OPT_FROM_CACHE;
                    message.sendToTarget();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String msg = response.body().string();
                            Message message = mHanler.obtainMessage();
                            message.what = MSG_PARSE_OPT_LIST;
                            message.obj = msg;
                            message.sendToTarget();
                    } else {
                        Message message = mHanler.obtainMessage();
                        message.what = MSG_PARSE_OPT_FROM_CACHE;
                        message.sendToTarget();
                    }
                }
            });
		} else {
             parseOptListFromCache();
              resizeOptListView();//hsu
		}
    }

    private void resizeOptListView() {
        if(mOptListView != null) {
            //LogTool.e("MarketFragment:getOptList,size ="+mOptId.size());
            mOptListView.setOptInitData(mOptId, mOptDataList);
        }
    }
    private void parseOptList(String msg) {
        mOptId.clear();
		mOptNames.clear();//hsu
		mOptDataList.clear();
        JSONObject json = JsnTool.getObject(msg);
        try {
            if((json != null)&&(json.getInt("status") ==1)&&(json.getJSONArray("data")!=null)) {
                JSONArray ary = json.getJSONArray("data");
				mOptStr = "";
                for(int i=0;i<ary.length();i++) {
                    JSONObject obj = ary.getJSONObject(i);

					String id= JsnTool.getString(obj,"quoteid");
					String label = JsnTool.getString(obj,"label");
					String name = JsnTool.getString(obj,"name");

                    //begin add by hsu.2015/12/23
                    //对自选进行校订，防止无效的数据（因为服务器允许无效的数据上传上去）
                    //LogTool.e("MarketFragment:parseOptList,i ="+i+",label="+label);
                    if(StrTool.areEmpty(label)||label.equals("null")) {
                        continue;
                    }
                    //end add by hsu

                    mOptId.add(id);
					mOptNames.add(name);//hsu

					MarketDataItem dataItem = new MarketDataItem(id,name,label);
					mOptDataList.put(id,dataItem);

					//begin mod by Tianjunhsu,2015/12/23
                    //防止mOptStr以","开头
                    /*if(i == 0) {
					    mOptStr = id+"#"+label+"#"+name;
					} else {
                        mOptStr = mOptStr+","+id+"#"+label+"#"+name;
					}*/
                    if(mOptStr.trim().isEmpty()) {
                        mOptStr = id+"#"+label+"#"+name;
                    } else {
                        mOptStr = mOptStr+","+id+"#"+label+"#"+name;
                    }
                    //end add by hsu
                }
				saveOptStr();
            } else {
                parseOptListFromCache();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            parseOptListFromCache();
        }
        //initOptDataListKey();
    }
    private void parseOptListFromCache() {

		mOptId.clear();
		mOptDataList.clear();
        
		//begin,add by hsu
		mOptStr = SharedPrefsUtil.getStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_OPT, null);
        LogTool.e("mOptStr ="+mOptStr);
        if(StrTool.areEmpty(mOptStr)) {
            return;
        }
        String items[] = mOptStr.split(",");
        LogTool.e("items.length ="+items.length);
        for(int i=0;i<items.length;i++) {
            LogTool.e("items[i]:"+i+","+items[i]);
            String attrs[] = items[i].split("#");

            //begin add by hsu.2015/12/23
            //对自选进行校订，防止无效的数据（因为服务器允许无效的数据上传上去）
            //LogTool.e("MarketFragment:parseOptListFromCache,i ="+i+",label="+attrs[1]);
            if(StrTool.areEmpty(attrs[1])||attrs[1].equals("null")) {
                continue;
            }
            //end add by hsu

			mOptId.add(attrs[0]);

			MarketDataItem dataItem = new MarketDataItem(attrs[0],attrs[2],attrs[1]);//attrs[0]是id,attrs[1]是label,attrs[2]是name
            mOptDataList.put(attrs[0],dataItem);
		}
        //end add by hsu

        //initOptDataListKey();
    }
	private void saveOptStr() {
        SharedPrefsUtil.putStringValue(LizoneApp.getApp(),SharedPrefsUtil.PREFS_OPT,mOptStr);
	}


    @Override
    public void onResume() {
        super.onResume();
        connect();
        expandListView();
        LogTool.v("MarketFragment:onResume");

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
        disconnected();//比较耗时，测试一下
        LogTool.v("MarketFragment:onPause");
    }
    @Override
    public void onStop() {
        super.onStop();
        LogTool.v("MarketFragment:onStop");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogTool.v("MarketFragment:onActivityCreated");
        mCache = LizoneApp.getACache();
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
		mOptListView.setOptData(mOptDataList);//add by hsu.2015/11/27
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
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }

    private void startKpointActivity(MarketDataItem item){
        Intent intent = new Intent(getActivity(), KPointActivity.class);
        Bundle mBundle = new Bundle();
       // mBundle.putSerializable("marketDataItem", item);
        mBundle.putParcelable("marketDataItem", item);
        intent.putExtras(mBundle);
        getActivity().startActivity(intent);
        //getActivity().startActivity(new Intent(getActivity(),EventActivity.class));
    }

    @Override
    public void onResult() {
        LogTool.d("MarketFragment:onResult.....");
    }

    private void setActivityResultObserver() {
        ((MainActivity) getActivity()).setActivityResultObserver(this);
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
                    LogTool.d(".......MarketFragment:setOnClickListener");
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
