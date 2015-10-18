package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cquant.lizone.R;
import com.cquant.lizone.tool.LogTool;

/**
 * Created by asus on 2015/8/30.
 */
public class ExploreFragment extends BaseFragment {

    private TabLayout tablayout;
    private ViewPager viewpager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.explore_fragment, container, false);
        tablayout = (TabLayout) root.findViewById(R.id.tablayout);
        viewpager= (ViewPager)root. findViewById(R.id.pager);
        TabLayout.Tab tab1 = tablayout.newTab().setText("签约高手");
        TabLayout.Tab tab2 = tablayout.newTab().setText("稳定收益");
        TabLayout.Tab tab3 = tablayout.newTab().setText("短线收益");
        TabLayout.Tab tab4 = tablayout.newTab().setText("月收益");
        TabLayout.Tab tab5 = tablayout.newTab().setText("年收益");
        tablayout.addTab(tab1);
        tablayout.addTab(tab2);
        tablayout.addTab(tab3);
        tablayout.addTab(tab4);
        tablayout.addTab(tab5);
        TabPagerAdapter adapter = new  TabPagerAdapter(getChildFragmentManager());
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);
        //viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("TianjunXu","ExploreFrag:onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("TianjunXu", "ExploreFrag:onPause");
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class TabPagerAdapter extends FragmentPagerAdapter {
        public final int COUNT = 5;
        private String[] titles = new String[]{"签约高手", "稳定收益", "短线收益", "月收益", "年收益"};

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Log.d("TianjunXu","getItem:position = "+position);
            switch (position) {
                case 0:
                    fragment = new SignMasterFragment();
                    //fragment = new SaginMasterFragmentTest();
                    break;
                case 1:
                    fragment = new StabMasterFragment();
                    break;
                case 2:
                    fragment = new ShortMasterFragment();
                    break;
                case 3:
                    fragment = new MonthMasterFragment();
                    break;
                case 4:
                    fragment = new YearMasterFragment();
                    break;
                default:
                    fragment = new SignMasterFragment();
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
