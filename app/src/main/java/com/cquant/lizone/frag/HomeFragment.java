package com.cquant.lizone.frag;

import android.content.Context;
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

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBPageChangeListener;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.PromotionItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.view.IconPagerAdapter;
import com.cquant.lizone.view.TabPageIndicator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by asus on 2015/8/30.
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";

    private RequestQueue mVolleyQueue;
    private ImageLoader mImageLoader;

    private ConvenientBanner mCBView;
    private FrameLayout mFyCB;
    private TextView mPromotionClickSum;
    private  ArrayList<PromotionItem> mPromotionList;

    private ACache mACache;

    private static final String[] CONTENT = new String[] { "订阅动态", "高手动态" };
    private static final int[] ICONS = new int[] {
            R.drawable.perm_group_down,
            R.drawable.perm_group_down_two
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mACache = LizoneApp.getACache();
        mVolleyQueue = Volley.newRequestQueue(getActivity());
        mImageLoader = new ImageLoader(mVolleyQueue, new BitmapCache());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        mFyCB = (FrameLayout)root.findViewById(R.id.cb_view);
        mCBView = (ConvenientBanner)root.findViewById(R.id.convenientBanner);
        mPromotionClickSum = (TextView)root.findViewById(R.id.promotion_click_num);

        FragmentPagerAdapter adapter = new DynamicAdapter(getActivity().getSupportFragmentManager());

        ViewPager pager = (ViewPager)root.findViewById(R.id.pager);
        pager.setAdapter(adapter);

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
        Log.d("TianjunXu", "size =" + mPromotionList.size());
        mCBView.setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused},new CBPageItemSelected());
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

    public class BitmapCache implements ImageLoader.ImageCache {

        @Override
        public Bitmap getBitmap(String s) {
            return mACache.getAsBitmap(s);
        }

        @Override
        public void putBitmap(String s, Bitmap bitmap) {
            mACache.put(s,bitmap);

        }
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
            //ImageLoader.getInstance().displayImage(data,imageView);
            mImageLoader.get(data.img_url, new FadeInImageListener(imageView,getActivity()));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击事件
                    //Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class FadeInImageListener implements ImageLoader.ImageListener {

        WeakReference<ImageView> mImageView;
        Context mContext;

        public FadeInImageListener(ImageView image,Context context) {
            mImageView = new WeakReference<ImageView>(image);
            mContext = context;
        }

        @Override
        public void onErrorResponse(VolleyError arg0) {
            if(mImageView.get() != null) {
                mImageView.get().setImageResource(R.mipmap.ic_launcher);
            }
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
            if(mImageView.get() != null) {
                ImageView image = mImageView.get();
                if(response.getBitmap() != null) {
                    image.setImageBitmap(response.getBitmap());
                } else {
                    image.setImageResource(R.mipmap.ic_launcher);
                }
            }
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

    class DynamicAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        public  DynamicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DynamicFragment.newInstance(CONTENT[position % CONTENT.length]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // return CONTENT[position % CONTENT.length].toUpperCase();
            return CONTENT[position % CONTENT.length];
        }

        @Override public int getIconResId(int index) {
            return ICONS[index];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
