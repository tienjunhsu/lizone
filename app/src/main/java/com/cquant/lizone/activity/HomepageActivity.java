package com.cquant.lizone.activity;

import android.os.Bundle;
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

import com.cquant.lizone.R;
import com.cquant.lizone.bean.HomepageHeadItem;
import com.cquant.lizone.frag.AccountPositionFragment;
import com.cquant.lizone.frag.AccountRecordFragment;
import com.cquant.lizone.frag.HomepagePositionFragment;
import com.cquant.lizone.frag.HomepageRecordFragment;
import com.cquant.lizone.frag.HomepageStatFragment;
import com.cquant.lizone.frag.HomepageViewFragment;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC on 2015/9/17.
 */
public class HomepageActivity extends BaseActivity {

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

    private WebHelper mWebhelper = null;
    private HomepageHeadItem headerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_activity);
        //初始化控件及布局
        initView();

        user_id = getIntent().getStringExtra("user_id");
        header_url = Utils.BASE_URL +"UserHomeData/uid/" +user_id;
        mWebhelper = new WebHelper(this);

       // AppBarLayout mAppBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
        //mAppBarLayout.
        
        getHeaderData();
    }

    public String getUserId() {
        return user_id;
    }
    private void getHeaderData() {
        mWebhelper.doLoadGet(header_url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.v(" HomePageActivity ->getHeaderData,gonWebFinished:success = " + success + ",msg =" + msg);
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parseHeaderData(msg);
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
