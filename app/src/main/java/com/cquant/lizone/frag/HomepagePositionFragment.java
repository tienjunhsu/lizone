package com.cquant.lizone.frag;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cquant.lizone.R;
import com.cquant.lizone.activity.HomepageActivity;
import com.cquant.lizone.bean.HomepagePositionItem;
import com.cquant.lizone.bean.HomepageRecordItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepagePositionFragment extends BaseFragment{
    private static final String TAG = "HomepagePositionFragment";

    protected String url = Utils.BASE_URL+"UserChic_list/uid/";
    protected WebHelper mWebhelper = null;

    private ArrayList<HomepagePositionItem> mPositionList;

    protected RecyclerView mRecyclerView;
    private MasterAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.default_fragment, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        url = url+((HomepageActivity)getActivity()).getUserId();
        mWebhelper = new WebHelper(getActivity());
        initList();
        initReRecyclerView();
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        getPosition();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if( mWebhelper != null) {
            if (isVisibleToUser) {
                //相当于Fragment的onResume,当前可见
                LogTool.v(TAG + "setUserVisibleHint to true");
                getPosition();
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
        mPositionList = new ArrayList<HomepagePositionItem>();
    }
    private void getPosition() {
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.v(TAG + "Position gonWebFinished:success = " + success + ",msg =" + msg);
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parsePosition(msg);
                    }
                }
            }
        });
    }

    private void parsePosition(String msg) {
        mPositionList =HomepagePositionItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
    }

    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.homepage_position_list_item,viewGroup,false);
            RecordViewHolder vh = new RecordViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            RecordViewHolder holder = (RecordViewHolder) viewHolder;
            holder.mTvName.setText(mPositionList.get(i).ex_name);
            holder.mTvCode.setText("("+mPositionList.get(i).ex_code+")");
            holder.mTvPrice.setText("当前价："+mPositionList.get(i).newprice);
            holder.mTvOpenPrice.setText("成本价："+mPositionList.get(i).start_price);
            holder.mTvNum.setText("持仓量："+mPositionList.get(i).ex_number);
            holder.mTvCapital.setText("持仓市值："+mPositionList.get(i).capital);
            holder.mTvProfit.setText("浮动盈亏："+mPositionList.get(i).profit_number);
            holder.mTvYield.setText("盈亏比例："+mPositionList.get(i).yield);

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

        }

        @Override
        public int getItemCount() {
            return mPositionList.size();
        }

        class RecordViewHolder extends RecyclerView.ViewHolder{

            public TextView mTvName;
            public TextView mTvCode;
            public TextView mTvPrice;
            public TextView mTvOpenPrice;
            public TextView mTvNum;
            public TextView mTvCapital;
            public TextView mTvProfit;
            public TextView mTvYield;

            public RecordViewHolder(View itemView) {
                super(itemView);

                mTvName = (TextView) itemView.findViewById(R.id.ex_name);
                mTvCode = (TextView) itemView.findViewById(R.id.ex_code);
                 mTvPrice= (TextView) itemView.findViewById(R.id.new_price);
                 mTvOpenPrice= (TextView) itemView.findViewById(R.id.start_price);
               mTvNum= (TextView) itemView.findViewById(R.id.tv_num);
                mTvCapital= (TextView) itemView.findViewById(R.id.tv_capital);
              mTvProfit= (TextView) itemView.findViewById(R.id.tv_profit);
                 mTvYield= (TextView) itemView.findViewById(R.id.tv_yield);

            }
        }
    }

}
