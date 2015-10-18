package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.activity.HomepageActivity;
import com.cquant.lizone.bean.HomepageRecordItem;
import com.cquant.lizone.bean.TradeRecordItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepageRecordFragment extends BaseFragment {
    private static final String TAG = "HomepageRecordFragment";

    protected String url = Utils.BASE_URL+"UserJiaoy_list/uid/";
    protected WebHelper mWebhelper = null;

    protected ArrayList<HomepageRecordItem> mRecordList;

    protected RecyclerView mRecyclerView;
    private MasterAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.default_fragment, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        url = url+((HomepageActivity)getActivity()).getUserId();
        initList();
        initReRecyclerView();
        mWebhelper = new WebHelper(getActivity());
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        getRecord();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mWebhelper != null) {
            if (isVisibleToUser) {
                //相当于Fragment的onResume,当前可见
                LogTool.v(TAG + "setUserVisibleHint to true");

                getRecord();
            } else {
                mWebhelper.cancleRequest();
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    private void initReRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MasterAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(), R.drawable.default_recylerview_divider));
    }

    private void initList() {
        mRecordList = new ArrayList<HomepageRecordItem>();
    }
    private void getRecord() {
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.v(TAG+"getRecord gonWebFinished:success = " + success + ",msg =" + msg);
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parseRecord(msg);
                    }
                }
            }
        });
    }

    private void parseRecord(String msg) {
        mRecordList =HomepageRecordItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
    }

    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.homepage_record_frag,viewGroup,false);
            RecordViewHolder vh = new RecordViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            RecordViewHolder holder = (RecordViewHolder) viewHolder;
            holder.mTvName.setText(mRecordList.get(i).ex_name);
            holder.mTvCode.setText("("+mRecordList.get(i).ex_code+")");
            holder.mTvBody.setText(mRecordList.get(i).text);
            holder.mTvEndTime.setText(mRecordList.get(i).time);

        }

        @Override
        public int getItemCount() {
            return mRecordList.size();
        }

        class RecordViewHolder extends RecyclerView.ViewHolder{

            public TextView mTvName;
            public TextView mTvCode;
            public TextView mTvBody;
            public TextView mTvEndTime;

            public RecordViewHolder(View itemView) {
                super(itemView);

                mTvName = (TextView) itemView.findViewById(R.id.ex_name);
                mTvCode = (TextView) itemView.findViewById(R.id.ex_code);
                mTvBody = (TextView) itemView.findViewById(R.id.body);
                mTvEndTime = (TextView) itemView.findViewById(R.id.time);
            }
        }
    }

}
