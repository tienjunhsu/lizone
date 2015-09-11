package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountPositionFragment extends BaseFragment {
    private static final String TAG = "AccountPositionFragment";

    private ACache mACache;

    private String url = Utils.BASE_URL+"Chicang_list/";
    private WebHelper mWebhelper = null;

    //private ArrayList<DynamicItem> mDynamicList;
    private String mFileName;

    private RecyclerView mRecyclerView;
    //private DynamicAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mACache = LizoneApp.getACache();
        mFileName = Md5FileNameGenerator.generate(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_activity, container, false);
        return root;
    }
    private void getPosition() {
        Log.d("TianjunXu", " getPosition:url = " + url);
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                Log.d("TianjunXu"," getPosition:success = "+success+",msg ="+msg);
                if(success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if((response != null)&&(JsnTool.getInt(response,"status")==1))  {

                    }
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        getPosition();
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
        mWebhelper = new WebHelper(getActivity());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
