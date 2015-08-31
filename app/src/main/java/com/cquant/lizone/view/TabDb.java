package com.cquant.lizone.view;

import com.cquant.lizone.R;
import com.cquant.lizone.frag.ExploreFragment;
import com.cquant.lizone.frag.HomeFragment;
import com.cquant.lizone.frag.MarketFragment;

/**
 * Created by asus on 2015/8/30.
 */
public class TabDb {
    public static int[] getTabsTxt(){
        int[] tabs={R.string.home_title,R.string.market_title,R.string.explore_title};
        return tabs;
    }
    public static int[] getTabsImg(){
        int[] ids={R.drawable.ic_home_normal,R.drawable.ic_market_normal,R.drawable.ic_explore_normal};
        return ids;
    }
    public static int[] getTabsImgLight(){
        int[] ids={R.drawable.ic_home_normal,R.drawable.ic_market_pressed,R.drawable.ic_explore_pressed};
        return ids;
    }
    public static Class[] getFragments(){
        Class[] clz={HomeFragment.class,MarketFragment.class, ExploreFragment.class};
        return clz;
    }
}
