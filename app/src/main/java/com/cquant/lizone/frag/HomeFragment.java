package com.cquant.lizone.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBPageChangeListener;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.activity.EventActivity;
import com.cquant.lizone.activity.LoginActivity;
import com.cquant.lizone.activity.WebPageActivity;
import com.cquant.lizone.bean.PromotionItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.view.IconPagerAdapter;
import com.cquant.lizone.view.TabPageIndicator;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by asus on 2015/8/30.
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";

    private ConvenientBanner mCBView;
    private FrameLayout mFyCB;
    private TextView mPromotionClickSum;
    private TextView mMoreEvent;
    private  ArrayList<PromotionItem> mPromotionList;

    private ACache mACache;
    private ViewPager pager;

    private static final String[] CONTENT = new String[] { "订阅动态", "高手动态" };

    private static final String[] F_CONTENT = new String[] { "sub", "master" };

    private View.OnClickListener onMoreEventListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if(LizoneApp.mCanLogin ) {
                openEventActivity();
            }else {
                gotoLogin();
            }
        }
    };
    private void gotoLogin() {
        getActivity().startActivity(new Intent(getActivity(),LoginActivity.class));
    }
    private void openEventActivity() {
        getActivity().startActivity(new Intent(getActivity(),EventActivity.class));

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mACache = LizoneApp.getACache();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        mFyCB = (FrameLayout)root.findViewById(R.id.cb_view);
        mCBView = (ConvenientBanner)root.findViewById(R.id.convenientBanner);
        mPromotionClickSum = (TextView)root.findViewById(R.id.promotion_click_num);

        mMoreEvent = (TextView)root.findViewById(R.id.tv_more);
        mMoreEvent.setOnClickListener(onMoreEventListener);

       // FragmentPagerAdapter adapter = new DynamicAdapter(getActivity().getSupportFragmentManager());
        FragmentPagerAdapter adapter = new DynamicAdapter(getChildFragmentManager());

        pager = (ViewPager)root.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);

        TabPageIndicator indicator = (TabPageIndicator)root.findViewById(R.id.indicator);
        indicator.setViewPager(pager);


        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        initCBView();
    }

    private void initCBView() {
        String mPromotions = SharedPrefsUtil.getStringValue(getActivity(),SharedPrefsUtil.PREFS_PROMOTIONS,null);
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
       },mPromotionList);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TianjunXu", "HomeFrag:onResume,position = "+pager.getCurrentItem());
    }
    @Override
    public void onPause() {
        super.onPause();
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
    private void startEventPageActivity(int position) {
        Intent intent = new Intent(getActivity(), WebPageActivity.class);
        intent.putExtra("title",mPromotionList.get(position).name);
        intent.putExtra("web_addr",mPromotionList.get(position).url);
        getActivity().startActivity(intent);
    }

    private class CBPageItemSelected implements CBPageChangeListener.OnCBPageSelected {

        @Override
        public void onPageSelected(int index) {
            if(mPromotionClickSum != null) {
                mPromotionClickSum.setText(mPromotionList.get(index).sum);
            }
        }
    }

    class DynamicAdapter extends FragmentPagerAdapter {
        public  DynamicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DynamicFragment.newInstance(F_CONTENT[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return CONTENT[position % CONTENT.length].toUpperCase();
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
