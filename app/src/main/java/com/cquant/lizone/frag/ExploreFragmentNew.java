package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cquant.lizone.R;
import com.cquant.lizone.tool.LogTool;

/**
 * Created by asus on 2015/10/18.
 */
public class ExploreFragmentNew extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.explore_fragment_new, container, false);
        //viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        ;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TianjunXu", "ExploreFrag:onResume");
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
    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.text1:
                LogTool.d("click text1");
                break;
            case R.id.text2:
                LogTool.d("click text2");
                break;
            case R.id.text3:
                LogTool.d("click text3");
                break;
            case R.id.text4:
                LogTool.d("click text4");
                break;
            case R.id.text5:
                LogTool.d("click text5");
                break;
            default:
                break;
        }
    }
}
