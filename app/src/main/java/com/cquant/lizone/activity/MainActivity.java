package com.cquant.lizone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.view.CircleImageView;
import com.cquant.lizone.view.TabDb;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by asus on 2015/8/30.
 */
public class MainActivity extends BaseActivity implements OnTabChangeListener {

    private static final String TAG = "MainActivity";

    private FragmentTabHost tabHost;
    private DrawerLayout mDrawerLayout;
    //private NavigationView mNavigationView;
    private NavigationView mNavigationView;
    private Toolbar toolbar;
    private TextView mTvTitle;
    private CircleImageView mImgLogo;
    private Button mActionMenu;

    //navigation header view
    private  CircleImageView mNavIvHead;
    private TextView mNavTvName;
    private TextView mNavTvSign;
    private TextView mNavTvSub;
    private TextView mNavTvFans;
    private TextView mNavTvView;
    private TextView mNavTvPoint;

    private LinearLayout mLyNavNormal;
    private LinearLayout mLyNavLogin;

    private DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            //DO NOTING
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            //refresh info
            LogTool.d(TAG + "onDrawerOpened");
            if(LizoneApp.mCanLogin ) {
                mLyNavLogin.setVisibility(View.GONE);
                mLyNavNormal.setVisibility(View.VISIBLE);
                refreshInfo();
            } else {
                mLyNavNormal.setVisibility(View.GONE);
                mLyNavLogin.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            //DO NOTHING
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            //DO NOTHING
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_new);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        //mDrawerLayout.setDrawerListener(mDrawerListener);
       // mNavigationView = (NavigationView) findViewById(R.id.id_nv_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvTitle = (TextView)findViewById(R.id.actionbar_title);
        mImgLogo = (CircleImageView)findViewById(R.id.actionbar_ic);
        mActionMenu = (Button)findViewById(R.id.actionbar_menu);

        mNavIvHead =(CircleImageView)findViewById(R.id.nav_iv_portrait);
        mNavTvName = (TextView)findViewById(R.id.nav_tv_name);
        mNavTvSign = (TextView)findViewById(R.id.nav_ev_inf);
        mNavTvSub = (TextView)findViewById(R.id.nav_tv_sub_num);
        mNavTvFans = (TextView)findViewById(R.id.nav_tv_fans_num);
        mNavTvView = (TextView)findViewById(R.id.nav_tv_idear_num);
        mNavTvPoint = (TextView)findViewById(R.id.nav_menu_tv_point);
        
        tabHost=(FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager()
                , R.id.contentLayout);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(this);

        mLyNavNormal =(LinearLayout)findViewById(R.id.nav_head_normal);
        mLyNavLogin =(LinearLayout)findViewById(R.id.nav_head_login);

        initToolBar();
        initTab();
       // setupDrawerContent(mNavigationView);

    }

    private void setupDrawerContent(NavigationView mNavigationView) {
        /*mNavigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener()
                {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });*/
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("");
        //toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        mTvTitle.setVisibility(View.VISIBLE);
        mImgLogo.setVisibility(View.VISIBLE);
        mActionMenu.setVisibility(View.VISIBLE);
        mImgLogo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(LizoneApp.mCanLogin ) {
                    mLyNavLogin.setVisibility(View.GONE);
                    mLyNavNormal.setVisibility(View.VISIBLE);
                    refreshInfo();
                } else {
                    mLyNavNormal.setVisibility(View.GONE);
                    mLyNavLogin.setVisibility(View.VISIBLE);
                }
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        mActionMenu.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(LizoneApp.mCanLogin ) {
                    openAccountActivity();
                } else {
                    gotoLogin();
                }
            }
        });
        super.initToolBar(toolbar);
       // final ActionBar ab = getSupportActionBar();
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
       // ab.setDisplayHomeAsUpEnabled(true);

    }

    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.nav_lv_open_account:
                if(LizoneApp.mCanLogin ) {
                    openFirmAccount();
                } else {
                    gotoLogin();
                }
                break;
            case R.id.nav_lv_point:
                if(LizoneApp.mCanLogin ) {
                    openPonitActivity();
                } else {
                    gotoLogin();
                }
                break;
            case R.id.nav_lv_account:
                if(LizoneApp.mCanLogin ) {
                    openAccountActivity();
                } else {
                    gotoLogin();
                }
                break;
            case R.id.nav_lv_event:
                if(LizoneApp.mCanLogin ) {
                    openEventActivity();
                }else {
                    gotoLogin();
                }
                break;
            case R.id.nav_lv_gift:
                if(LizoneApp.mCanLogin ) {
                    openGiftActivity();
                }else {
                    gotoLogin();
                }
                break;
            case R.id.nav_lv_setting:
                openSettingActivity();
                break;
            case R.id.nav_head_login:
                gotoLogin();
                break;
            case R.id.nav_head_normal:
                if(LizoneApp.mCanLogin ) {
                    openAccountActivity();
                }
                break;
            default:
                break;
        }
    }

    private void openSettingActivity() {
        startActivity(new Intent(this,SettingActivity.class));
        mDrawerLayout.closeDrawers();
    }

    private void gotoLogin() {
        startActivity(new Intent(this,LoginActivity.class));
        mDrawerLayout.closeDrawers();
    }

    private void openPonitActivity() {
        startActivity(new Intent(this, MyPointActivity.class));
        mDrawerLayout.closeDrawers();
    }

    private void openAccountActivity() {
        startActivity(new Intent(this,AccountActivity.class));
        mDrawerLayout.closeDrawers();
    }
    private void openEventActivity() {
        startActivity(new Intent(this,EventActivity.class));
        mDrawerLayout.closeDrawers();

    }
    private void openGiftActivity() {

    }
    private void openFirmAccount() {
        startActivity(new Intent(this,OpenFirmAccountActivity.class));
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initTab() {
        int tabs[]= TabDb.getTabsTxt();
        for(int i=0;i<tabs.length;i++){
            TabHost.TabSpec tabSpec=tabHost.newTabSpec(getString(tabs[i])).setIndicator(getTabView(i));
            tabHost.addTab(tabSpec, TabDb.getFragments()[i],null);
            tabHost.setTag(i);
        }
    }
    private View getTabView(int idx){
        View view= LayoutInflater.from(this).inflate(R.layout.footer_tabs, null);
        ((TextView)view.findViewById(R.id.tvTab)).setText(TabDb.getTabsTxt()[idx]);
        if(idx==0){
            ((TextView)view.findViewById(R.id.tvTab)).setTextColor(getResources().getColor(R.color.blue_two));
            ((ImageView)view.findViewById(R.id.ivImg)).setImageResource(TabDb.getTabsImgLight()[idx]);
        }else{
            ((ImageView)view.findViewById(R.id.ivImg)).setImageResource(TabDb.getTabsImg()[idx]);
        }
        return view;
    }
    @Override
    public void onTabChanged(String tabId) {
        updateTab();
    }

    private void updateTab(){
        TabWidget tabw=tabHost.getTabWidget();
        for(int i=0;i<tabw.getChildCount();i++){
            View view=tabw.getChildAt(i);
            ImageView iv=(ImageView)view.findViewById(R.id.ivImg);
            if(i==tabHost.getCurrentTab()){
                ((TextView)view.findViewById(R.id.tvTab)).setTextColor(getResources().getColor(R.color.blue_two));
                iv.setImageResource(TabDb.getTabsImgLight()[i]);
                view.setBackgroundColor(getResources().getColor(R.color.tab_item_pressed));
                setToolBarTitle(i);//2015/10/19
            }else{
                ((TextView)view.findViewById(R.id.tvTab)).setTextColor(getResources().getColor(R.color.white_two));
                iv.setImageResource(TabDb.getTabsImg()[i]);
                view.setBackgroundColor(getResources().getColor(R.color.tab_item_normal));
            }

        }
    }
    private void setToolBarTitle(int i) {
        mTvTitle.setText(TabDb.getTabsTxt()[i]);
    }

    private void refreshInfo() {
        if(GlobalVar.sAccountInf == null) {
            return;
        }
        LogTool.d(TAG+"refreshInfo:sAccountInf not null");
        ImageLoader.getInstance().displayImage(GlobalVar.sAccountInf.photo, mNavIvHead);
        mNavTvName.setText(GlobalVar.sAccountInf.name);
        mNavTvSign.setText(GlobalVar.sAccountInf.profile);
        mNavTvSub.setText(GlobalVar.sAccountInf.sub_num);
        mNavTvFans.setText(GlobalVar.sAccountInf.fans_num);
        mNavTvView.setText(GlobalVar.sAccountInf.view_num);
        mNavTvPoint.setText(GlobalVar.sAccountInf.integral);

    }
    private void refreshLogo() {
        if(GlobalVar.sAccountInf == null) {
            LogTool.d(TAG+"refreshLogo:sAccountInf null");
            mImgLogo.setImageResource(R.drawable.default_head);
        } else {
            LogTool.d(TAG+"refreshLogo:sAccountInf not null");
            ImageLoader.getInstance().displayImage(GlobalVar.sAccountInf.photo,mImgLogo);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshLogo();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) { //按下的如果是BACK，mDrawerLayout是打开的，关闭
            //do something here
            if(mDrawerLayout.isDrawerOpen(Gravity.LEFT) ){
                mDrawerLayout.closeDrawers();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogTool.d("MainActivity:onActivityResult");
        if(requestCode == EDIT_OPT_CODE) {
            if(mActivityResultObserver != null) {
                mActivityResultObserver.onResult();
            }
            if(resultCode == RESULT_OK) {
                /*android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                manager.getFragments().size();*/
            }
        }
    }

    private OnActivityResultObserver mActivityResultObserver;
    public void setActivityResultObserver(OnActivityResultObserver observer) {
        mActivityResultObserver = observer;
    }
    public interface OnActivityResultObserver{
        public void onResult();
    }
}
