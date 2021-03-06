package com.cquant.lizone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.HomepageHeadItem;
import com.cquant.lizone.frag.AccountPositionFragment;
import com.cquant.lizone.frag.AccountRecordFragment;
import com.cquant.lizone.frag.HomepagePositionFragment;
import com.cquant.lizone.frag.HomepageRecordFragment;
import com.cquant.lizone.frag.HomepageStatFragment;
import com.cquant.lizone.frag.HomepageViewFragment;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2015/9/17.
 */
public class HomepageActivity extends BaseActivity {

    private static final String TAG = "HomepageActivity";

    //将ToolBar与TabLayout结合放入AppBarLayout
    private Toolbar mToolbar;
    //Tab菜单，主界面上面的tab切换菜单
    private TabLayout mTabLayout;
    //v4中的ViewPager控件
    private ViewPager mViewPager;

    private ImageView mHeadView;
    private TextView mName;
    private TextView mIntroduction;
    private TextView mFansNum;
    private TextView mSubNum;
    private TextView mTradeNum;
    private TextView mProfiteRate;

    private Button mBtnSub;
    private Button mBtnFollow;

    private String user_id;

    private String header_url;

    private HomepageHeadItem headerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        //初始化控件及布局
        initView();

        user_id = getIntent().getStringExtra("user_id");
        header_url = Utils.BASE_URL +"UserHomeData/uid/" +user_id;

       // AppBarLayout mAppBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        //mAppBarLayout.
        
        getHeaderData();
    }

    public String getUserId() {
        return user_id;
    }
    private void getHeaderData() {
        Request request = new Request.Builder().url(header_url).tag(TAG).build();
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
                    LogTool.e("addAgree_ok:onResponse->msg=" + msg);
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_PARSE_HEADER_DATA;
                            message.obj = msg;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void parseHeaderData(String msg) {
        headerData = HomepageHeadItem.getItem(msg);
        refreshHeader();
    }

    private void refreshHeader() {
        if(!TextUtils.isEmpty(headerData.photo)) {
            ImageLoader.getInstance().displayImage(headerData.photo,mHeadView);
        }
        mName.setText(headerData.name);
        mIntroduction.setText(headerData.profile);
        mFansNum.setText(headerData.fans);
        mSubNum.setText(headerData.sub_num);
        mTradeNum .setText(headerData.trade_num);
        mProfiteRate.setText(headerData.yield);

        setSubBtnText();
        setFollowBtnText();

        if(headerData.gend ==1) {
            mBtnFollow.setText("取消跟单");
        } else {
            mBtnFollow.setText("跟单");
        }
        if(headerData.dingys == 1) {
            mBtnSub.setText("取消订阅");
        } else {
            mBtnSub.setText("+订阅");
        }
    }

    private void initView() {
        mToolbar = (Toolbar)findViewById(R.id.tool_bar);
        mToolbar.setTitle("个人主页");
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTabLayout = (TabLayout) this.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) this.findViewById(R.id.pager);

        mHeadView = (ImageView) findViewById(R.id.photo);
        mName = (TextView) findViewById(R.id.name);
        mIntroduction = (TextView) findViewById(R.id.introduction);
        mFansNum = (TextView) findViewById(R.id.fans_num);
        mSubNum = (TextView) findViewById(R.id.sub_num);
        mTradeNum = (TextView) findViewById(R.id.trade_num);
        mProfiteRate = (TextView) findViewById(R.id.profit_rate);
        mBtnSub = (Button)findViewById(R.id.btn_sub);
        mBtnFollow = (Button)findViewById(R.id.btn_follow);

        mBtnSub.setOnClickListener(subBtnOnClickListener);
        mBtnFollow.setOnClickListener(followBtnOnClickListener);

        List<String> titles = new ArrayList<>();
        titles.add("统计");
        titles.add("交易记录");
        titles.add("当前持仓");
        titles.add("观点");
        //初始化TabLayout的title
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(3)));
        //add to test
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomepageStatFragment());
        fragments.add(new HomepageRecordFragment());
        fragments.add(new HomepagePositionFragment());
        fragments.add(new HomepageViewFragment());

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
        //

    }
    private View.OnClickListener subBtnOnClickListener =new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            if(LizoneApp.mCanLogin ) {
                subOrCancleSub();
            }  else {
                gotoLogin();
            }

        }
    };

    private void subOrCancleSub() {
        Request request = new Request.Builder().url(Utils.BASE_URL+"Dingyue/sub_id/"+user_id).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage();
                message.what = MSG_POP_MSG;
                if (headerData.dingys == 1) {
                    message.obj = "取消订阅失败";
                } else {
                    message.obj = "订阅失败";
                }
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_CHANGE_SUB_SUCCEED;
                            if (StrTool.areNotEmpty(JsnTool.getString(obj, "msg"))) {
                                message.obj= JsnTool.getString(obj, "msg");
                            }else {
                                if (headerData.dingys == 1) {
                                    message.obj = "取消订阅成功";
                                } else {
                                    message.obj = "订阅成功";
                                }
                            }
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_POP_MSG;
                        if (headerData.dingys == 1) {
                            message.obj = "取消订阅失败";
                        } else {
                            message.obj = "订阅失败";
                        }
                        message.sendToTarget();
                    }
                } else {
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_POP_MSG;
                    if (headerData.dingys == 1) {
                        message.obj = "取消订阅失败";
                    } else {
                        message.obj = "订阅失败";
                    }
                    message.sendToTarget();
                }
            }
        });
    }

    private void setSubBtnText() {
        if(headerData.dingys == 1) {
            mBtnSub.setText("取消订阅");
        } else {
            mBtnSub.setText("+订阅");
        }
    }
    private void setFollowBtnText() {
        if(headerData.gend ==1) {
            mBtnFollow.setText("取消跟单");
        } else {
            mBtnFollow.setText("跟单");
        }
    }
    private View.OnClickListener followBtnOnClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            if(LizoneApp.mCanLogin ) {
                if (headerData.gend == 1) {
                    cancleFollow();
                } else {
                    startFollow();
                }
            }else {
                gotoLogin();
            }
        }
    };

    private void startFollow() {
        Intent intent = new Intent(this,FollowSettingActivity.class);
        intent.putExtra("uid", user_id);
        //startActivity(intent);
        startActivityForResult(intent,FOLLOW_SETTING_CODE);
    }


    private void cancleFollow() {
        Request request = new Request.Builder().url(Utils.BASE_URL + "Cancel_Gend/gend_id/" + user_id).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage();
                message.what = MSG_POP_MSG;
                message.obj = "取消跟单失败";
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG__CANCLE_FOLLOW_SUCCESSED;
                            message.obj = obj;
                            message.sendToTarget();
                        } else {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_POP_MSG;
                            message.obj = "取消跟单失败";
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_POP_MSG;
                        message.obj = "取消跟单失败";
                        message.sendToTarget();
                    }
                } else {
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_POP_MSG;
                    message.obj = "取消跟单失败";
                    message.sendToTarget();
                }
            }
        });
    }
    private void cancleFollowSuccessed() {
        headerData.gend = 0;
        setFollowBtnText();
    }
    private void changeSubSucceed() {
        if (headerData.dingys == 1) {
            headerData.dingys = 0;
        } else if (headerData.dingys == 0) {
            headerData.dingys = 1;
        }
        setSubBtnText();
    }
    private static final int MSG_POP_MSG = 15;
    private static final int MSG__CANCLE_FOLLOW_SUCCESSED = 16;
    private static final int MSG_PARSE_HEADER_DATA =17;
    private static final int MSG_CHANGE_SUB_SUCCEED =18;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PARSE_HEADER_DATA:
                    parseHeaderData((String) msg.obj);
                    break;
                case MSG__CANCLE_FOLLOW_SUCCESSED:
                    cancleFollowSuccessed();
                    break;
                case MSG_CHANGE_SUB_SUCCEED:
                    popMsg((String) msg.obj);
                    changeSubSucceed();
                    break;
                case MSG_POP_MSG:
                    popMsg((String) msg.obj);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }
    private void gotoLogin() {
        startActivity(new Intent(this,LoginActivity.class));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == FOLLOW_SETTING_CODE) {
            if(resultCode == RESULT_OK) {
                headerData.gend = 1;
                setFollowBtnText();
            }
        }
    }
    public class FragmentAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;
        private List<String> mTitles;

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

}
