package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.ExploreListItem;
import com.cquant.lizone.bean.TradeRecordItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.CircleImageView;
import com.cquant.lizone.view.ItemDivider;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountRecordFragment extends BaseFragment {

    private static final String TAG = "AccountRecordFragment";

    protected String url = Utils.BASE_URL+"DealList/";
    protected WebHelper mWebhelper = null;

    protected ACache mACache;

    protected ArrayList<TradeRecordItem> mRecordList;
    protected String mFileName;

    protected RecyclerView mRecyclerView;
    private MasterAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileName = Md5FileNameGenerator.generate(url);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebhelper = new WebHelper(getActivity());
        mACache = LizoneApp.getACache();
        initList();
        initReRecyclerView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.default_fragment, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        getRecord();
        Log.d("TianjunXu", "AccountRecordFragment:onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("TianjunXu", "AccountRecordFragment:onPause");
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
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(), R.drawable.window_color_divider));
    }

    private void initList() {
        String list_str = mACache.getAsString(mFileName);
        if( list_str !=null) {
            mRecordList =TradeRecordItem.getItemList(list_str);
        } else {
            mRecordList = new ArrayList<TradeRecordItem>();
        }
    }
    private void getRecord() {
        Log.d("TianjunXu", " getRecord:url = " + url);
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                Log.d("TianjunXu", " gonWebFinished:success = " + success + ",msg =" + msg);
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
        mRecordList =TradeRecordItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
    }

    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.record_list_item,viewGroup,false);
            RecordViewHolder vh = new RecordViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            RecordViewHolder holder = (RecordViewHolder) viewHolder;
            holder.mTvName.setText(mRecordList.get(i).ex_name);
            holder.mTvCode.setText(mRecordList.get(i).ex_code);
            holder.mTvBeginDir.setText(mRecordList.get(i).start_label);
            holder.mTvBeginTime.setText(mRecordList.get(i).start_time);
            holder.mTvEndDir.setText(mRecordList.get(i).end_label);
            holder.mTvEndTime.setText(mRecordList.get(i).end_time);
            holder.mTvNum.setText(mRecordList.get(i).end_number);
            holder.mTvProfit.setText(mRecordList.get(i).profit);
            holder.mTvYield.setText(mRecordList.get(i).yield);
            // set text color
            if(StrTool.getDouble(mRecordList.get(i).profit) > 0) {
                holder.mTvProfit.setTextColor(getActivity().getResources().getColor(R.color.red_two));
                holder.mTvYield.setTextColor(getActivity().getResources().getColor(R.color.red_two));
            } else if(StrTool.getDouble(mRecordList.get(i).profit) < 0) {
                holder.mTvProfit.setTextColor(getActivity().getResources().getColor(R.color.green_two));
                holder.mTvYield.setTextColor(getActivity().getResources().getColor(R.color.green_two));
            } else {
                holder.mTvProfit.setTextColor(getActivity().getResources().getColor(R.color.white_two));
                holder.mTvYield.setTextColor(getActivity().getResources().getColor(R.color.white_two));
            }
            //

        }

        @Override
        public int getItemCount() {
            return mRecordList.size();
        }

        class RecordViewHolder extends RecyclerView.ViewHolder{

            public TextView mTvName;
            public TextView mTvCode;
            public TextView mTvBeginDir;
            public TextView mTvBeginTime;
            public TextView mTvEndDir;
            public TextView mTvEndTime;
            public TextView mTvProfit;
            public TextView mTvNum;
            public TextView mTvYield;

            public RecordViewHolder(View itemView) {
                super(itemView);

                mTvName = (TextView) itemView.findViewById(R.id.ex_name);
                mTvCode = (TextView) itemView.findViewById(R.id.ex_code);
                mTvBeginDir = (TextView) itemView.findViewById(R.id.begin_dir);
                mTvBeginTime = (TextView) itemView.findViewById(R.id.begin_time);
                mTvEndDir = (TextView) itemView.findViewById(R.id.end_dir);
                mTvEndTime = (TextView) itemView.findViewById(R.id.end_time);
                mTvNum = (TextView) itemView.findViewById(R.id.tv_num);
                mTvYield = (TextView) itemView.findViewById(R.id.tv_yield);
                mTvProfit  = (TextView) itemView.findViewById(R.id.tv_profit);
            }
        }
    }

}
