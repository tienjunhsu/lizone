package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.cquant.lizone.R;
import com.cquant.lizone.frag.EventFragment;

/**
 * Created by asus on 2015/10/20.
 */
public class EventActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tablayout;
    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar();
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager= (ViewPager)findViewById(R.id.pager);

        TabLayout.Tab tab1 = tablayout.newTab().setText("全部活动");
        TabLayout.Tab tab2 = tablayout.newTab().setText("我已参与");
        tablayout.addTab(tab1);
        tablayout.addTab(tab2);

        TabPagerAdapter adapter = new  TabPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);

    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.my_events);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    class TabPagerAdapter extends FragmentPagerAdapter {
        public final int COUNT = 2;
        private String[] titles = new String[]{"全部活动", "我已经参与"};

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = EventFragment.newInstance(0);
                    break;
                case 1:
                    fragment = EventFragment.newInstance(1);
                    break;
                default:
                    fragment = EventFragment.newInstance(0);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
