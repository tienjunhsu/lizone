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
import com.cquant.lizone.bean.PositionItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountPositionFragment extends BaseFragment {
    private static final String TAG = "AccountPositionFragment";


    private String url = Utils.BASE_URL+"Chicang_list/";
    protected WebHelper mWebhelper = null;

    protected ACache mACache;

    protected ArrayList<PositionItem> mPositionList;
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
        getPosition();
        Log.d("TianjunXu", "AccountPositionFragment:onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("TianjunXu", "AccountPisitionFragment:onPause");
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
        /*String list_str = mACache.getAsString(mFileName);
        if( list_str !=null) {
            mRecordList =TradeRecordItem.getItemList(list_str);
        } else {
            mRecordList = new ArrayList<TradeRecordItem>();
        }*/
        mPositionList = new ArrayList<PositionItem>();
    }
    private void getPosition() {
        Log.d("TianjunXu", " getPosition:url = " + url);
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
        mPositionList=PositionItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        //mACache.put(mFileName, msg);
    }

    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.position_list_item,viewGroup,false);
            PositionViewHolder vh = new PositionViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PositionViewHolder holder = (PositionViewHolder) viewHolder;
            holder.mTvName.setText(mPositionList.get(i).ex_name);
            holder.mTvUsed.setText("占用："+mPositionList.get(i).use_capital);
            holder.mTvOpenPrice.setText("建仓："+mPositionList.get(i).start_price);
            holder.mTvDir.setText("方向:"+mPositionList.get(i).ex_dir);
            holder.mTvNum.setText("交易量："+mPositionList.get(i).ex_number);
            holder.mTvPrice.setText("现价："+mPositionList.get(i).newprice);
            holder.mTvTime.setText(mPositionList.get(i).start_time);
            holder.mTvProfit.setText("盈亏:"+mPositionList.get(i).profit_number);
            holder.mTvYield.setText("收益率:"+mPositionList.get(i).yield);
            // set text colr
            if(StrTool.getDouble(mPositionList.get(i).profit_number) > 0) {
                holder.mTvProfit.setTextColor(getActivity().getResources().getColor(R.color.red_two));
                holder.mTvYield.setTextColor(getActivity().getResources().getColor(R.color.red_two));
            } else if(StrTool.getDouble(mPositionList.get(i).profit_number) < 0) {
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
            return mPositionList.size();
        }

        class PositionViewHolder extends RecyclerView.ViewHolder{

            public TextView mTvName;
            public TextView mTvUsed;
            public TextView mTvOpenPrice;
            public TextView mTvDir;
            public TextView mTvNum;
            public TextView mTvPrice;
            public TextView mTvTime;
            public TextView mTvProfit;
            public TextView mTvYield;

            public PositionViewHolder(View itemView) {
                super(itemView);

                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mTvUsed = (TextView) itemView.findViewById(R.id.tv_used);
                mTvOpenPrice = (TextView) itemView.findViewById(R.id.tv_open_price);
                mTvDir = (TextView) itemView.findViewById(R.id.tv_dir);
                mTvNum = (TextView) itemView.findViewById(R.id.tv_num);
                mTvPrice = (TextView) itemView.findViewById(R.id.tv_price);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_open_time);
                mTvProfit  = (TextView) itemView.findViewById(R.id.tv_profit);
                mTvYield = (TextView) itemView.findViewById(R.id.tv_profit_ratio);
            }
        }
    }

}
