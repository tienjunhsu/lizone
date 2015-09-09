package com.cquant.lizone.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.tool.ACache;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountPositionFragment extends BaseFragment {
    private static final String TAG = "AccountPositionFragment";

    private ACache mACache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mACache = LizoneApp.getACache();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_activity, container, false);
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
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
}
