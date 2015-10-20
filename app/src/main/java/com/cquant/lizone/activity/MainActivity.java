package com.cquant.lizone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.view.CircleImageView;
import com.cquant.lizone.view.TabDb;

/**
 * Created by asus on 2015/8/30.
 */
public class MainActivity extends BaseActivity implements OnTabChangeListener {

    private FragmentTabHost tabHost;
    private DrawerLayout mDrawerLayout;
    //private NavigationView mNavigationView;
    private NavigationView mNavigationView;
    private Toolbar toolbar;
    private TextView mTvTitle;
    private CircleImageView mImgLogo;
    private Button mActionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_new);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
       // mNavigationView = (NavigationView) findViewById(R.id.id_nv_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvTitle = (TextView)findViewById(R.id.actionbar_title);
        mImgLogo = (CircleImageView)findViewById(R.id.actionbar_ic);
        mActionMenu = (Button)findViewById(R.id.actionbar_menu);
        
        tabHost=(FragmentTabHost)super.findViewById(android.R.id.tabhost);
        tabHost.setup(this,super.getSupportFragmentManager()
                ,R.id.contentLayout);
        tabHost.getTabWidget().setDividerDrawable(null);
        tabHost.setOnTabChangedListener(this);
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
            default:
                break;
        }
    }

    private void gotoLogin() {
        startActivity(new Intent(this,LoginActivity.class));
        mDrawerLayout.closeDrawers();
    }

    private void openPonitActivity() {

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
}
