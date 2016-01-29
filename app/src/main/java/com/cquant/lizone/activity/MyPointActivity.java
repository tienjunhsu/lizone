package com.cquant.lizone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBPageChangeListener;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.GiftItem;
import com.cquant.lizone.bean.NewsItem;
import com.cquant.lizone.bean.PromotionItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by PC on 2015/9/11.
 */
public class MyPointActivity extends BaseActivity {
    private static final String TAG = "MyPointActivity";
    private Toolbar toolbar;
    private Button mActionMenu;
    private ConvenientBanner mCBView;
    private FrameLayout mFyCB;
    private TextView mPromotionClickSum;
    private  ArrayList<PromotionItem> mPromotionList;

    private ACache mACache;

    private TextView mTvPoint;
    private TextView mTvNum;
    private GridView gridView;
    private GiftAdapter mAdapter;
    private ArrayList<GiftItem> mGiftList;
    private String mFileName;
    private int num = 0;//可以兑换的礼品数

    //private String url = "http://www.lizone.net/index.php/api/index/gift_list/sel_id/0/";
	private String url = Utils.BASE_URL+"gift_list/sel_id/0/";//hsu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypoint_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionMenu = (Button)findViewById(R.id.actionbar_menu);
        mFyCB = (FrameLayout)findViewById(R.id.cb_view);
        mCBView = (ConvenientBanner)findViewById(R.id.convenientBanner);
        mPromotionClickSum = (TextView)findViewById(R.id.promotion_click_num);

        mTvPoint = (TextView)findViewById(R.id.tv_point);
        mTvNum = (TextView)findViewById(R.id.tv_num);
        gridView = (GridView)findViewById(R.id.gridview);
        initToolBar();
        initCBView();
        mFileName = Md5FileNameGenerator.generate(url);
        mACache = LizoneApp.getACache();
        initList();
        initGridView();
        refreshPoint();
    }
    @Override
    public void onResume() {
        super.onResume();
        getGifts();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LizoneApp.getOkHttpClient().cancel(TAG);
    }
    private void initGridView() {

        mAdapter = new GiftAdapter(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startExchangeActivity(i);
            }
        });
        gridView.setAdapter(mAdapter);
    }
    private void getGifts() {
        Request request = new Request.Builder().url(url).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    if ((obj != null) && (JsnTool.getInt(obj, "status") == 1)) {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_PARSE_GIFT;
                        message.obj = msg;
                        message.sendToTarget();
                    }
                }
            }
        });
    }
    private static final int MSG_PARSE_GIFT = 20;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PARSE_GIFT:
                    parseGifts((String) msg.obj);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private void startExchangeActivity(int position) {
        //Intent intent = new Intent(this, IntegralExchangeActivity.class);
        //intent.putExtra("gid", mGiftList.get(position).id);
        Intent intent = new Intent(this,WebPageActivity.class);
        intent.putExtra("title","礼品兑换");
        intent.putExtra("web_addr",Utils.BASE_URL+"jifen_more/gid/"+mGiftList.get(position).id);
        startActivity(intent);
    }
    private void parseGifts(String msg) {
        mGiftList =GiftItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        refreshNumView();
        mACache.put(mFileName, msg);
    }

    private void refreshNumView() {
        num = 0;
        int myPoint = Integer.valueOf(GlobalVar.sAccountInf.integral);
        for(GiftItem item:mGiftList){
            if(myPoint >= Integer.valueOf(item.price)) {
                ++num;
            }
        }
        mTvNum.setText(num + "");
    }
    private void refreshPoint(){
        if(GlobalVar.sAccountInf == null) {
            return;
        }
        mTvPoint.setText(GlobalVar.sAccountInf.integral);
    }

    private void initList() {
        String list_str = mACache.getAsString(mFileName);
        if( list_str !=null) {
            mGiftList=GiftItem.getItemList(list_str);
        } else {
            mGiftList = new ArrayList<GiftItem>();
        }
    }
    private void initCBView() {
        String mPromotions = SharedPrefsUtil.getStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_PROMOTIONS, null);
        if(mPromotions == null ){
            mFyCB.setVisibility(View.GONE);
            return;
        }
        mPromotionList = PromotionItem.getItemList( mPromotions);
        mCBView.setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused}, new CBPageItemSelected());
        if(mPromotionList.size() < 2) {
            mCBView.setPointViewVisible(false);
        }
        mCBView.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
            @Override
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView();
            }
        }, mPromotionList);
    }
    private void startEventPageActivity(int position) {
        Intent intent = new Intent(this, WebPageActivity.class);
        intent.putExtra("title",mPromotionList.get(position).name);
        intent.putExtra("web_addr", mPromotionList.get(position).url);
        startActivity(intent);
    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.my_points);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mActionMenu.setText("明细");
        //mActionMenu.setVisibility(View.VISIBLE);
        mActionMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });
        super.initToolBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.iv_rule:
                startRuleActivity();
                break;
            default:
                break;
        }
    }

    private void startRuleActivity() {
        Intent intent = new Intent(this,WebPageActivity.class);
        intent.putExtra("title","积分规则");
        intent.putExtra("web_addr", Utils.BASE_URL+"JifenGuize/");
        startActivity(intent);
    }

    public class NetworkImageHolderView implements CBPageAdapter.Holder<PromotionItem>{
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, PromotionItem data) {
            // imageView.setImageResource(R.drawable.ic_default_adimage);
            ImageLoader.getInstance().displayImage(data.img_url,imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击事件,打开活动页
                    startEventPageActivity(position);
                }
            });
        }
    }
    private class CBPageItemSelected implements CBPageChangeListener.OnCBPageSelected {

        @Override
        public void onPageSelected(int index) {
            if(mPromotionClickSum != null) {
                mPromotionClickSum.setText(mPromotionList.get(index).sum);
            }
        }
    }

    public class GiftAdapter extends BaseAdapter {

        private Context context=null;
        private LayoutInflater inflater=null;
        public GiftAdapter(Context context) {
            super();
            this.context = context;

            inflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mGiftList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        private class Holder{

            ImageView img=null;

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
//        获得holder以及holder对象中tv和img对象的实例
            Holder holder;
            if(convertView==null){


                convertView=inflater.inflate(R.layout.gift_list_item, null);
                holder=new Holder();
                holder.img=(ImageView) convertView.findViewById(R.id.img);

                convertView.setTag(holder);

            }else{
                holder=(Holder) convertView.getTag();

            }
            //String url = "http://www.lizone.net/Public/Uploads/gift/gift_min_2.jpg";
            //mGiftList.get(position).img
            ImageLoader.getInstance().displayImage(mGiftList.get(position).img,holder.img);
            return convertView;
        }

    }

}
