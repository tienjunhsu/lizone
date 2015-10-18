package com.cquant.lizone.frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.activity.HomepageActivity;
import com.cquant.lizone.bean.ExploreListItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/9/13.
 */
public class MasterListFragment extends BaseFragment {

    private static final String TAG = "MasterListFragment";

    protected String url = Utils.BASE_URL+"Faxian_list/aid/";
    protected WebHelper mWebhelper = null;

    protected ACache mACache;

    protected ArrayList<ExploreListItem> mMasterList;
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
        View root = inflater.inflate(R.layout.master_list_fragment, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        getMaster();
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
    }

    private void initList() {
        String list_str = mACache.getAsString(mFileName);
        if( list_str !=null) {
            mMasterList =ExploreListItem.getItemList(list_str);
        } else {
            mMasterList = new ArrayList<ExploreListItem>();
        }
    }
    private void getMaster() {
        Log.d("TianjunXu", " getMaster:url = " + url);
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                Log.d("TianjunXu", " gonWebFinished:success = " + success + ",msg =" + msg);
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parseMaster(msg);
                    }
                }
            }
        });
    }

    private void parseMaster(String msg) {
        mMasterList =ExploreListItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
    }

    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.master_list_item,viewGroup,false);
            MasterViewHolder vh = new MasterViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MasterViewHolder holder = (MasterViewHolder) viewHolder;
            holder.mTvName.setText(mMasterList.get(i).name);
            holder.mTvProfit.setText(mMasterList.get(i).yield);
            holder.mTvNum.setText(mMasterList.get(i).num);
            holder.mTvSuccess.setText(mMasterList.get(i).success);
            if(!TextUtils.isEmpty(mMasterList.get(i).photo)) {
                ImageLoader.getInstance().displayImage(mMasterList.get(i).photo,holder.mPhoto);
            }
        }

        @Override
        public int getItemCount() {
            return mMasterList.size();
        }

        class MasterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhoto;
            public TextView mTvName;
            public TextView mTvProfit;
            public TextView mTvNum;
            public TextView mTvSuccess;

            public MasterViewHolder(View itemView) {
                super(itemView);
                mPhoto = (ImageView) itemView.findViewById(R.id.photo);

                mTvName = (TextView) itemView.findViewById(R.id.name);
                mTvNum = (TextView) itemView.findViewById(R.id.trade_num);
                mTvSuccess = (TextView) itemView.findViewById(R.id.success_ratio);
                mTvProfit  = (TextView) itemView.findViewById(R.id.profit_ratio);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(v,getPosition());
            }
        }
    }
    private OnItemClickListener mOnClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v,int position) {
            startActivity(position);
        }
    };
    private void startActivity(int position) {
        Intent intent = new Intent(getActivity(), HomepageActivity.class);
        intent.putExtra("user_id", mMasterList.get(position).id);
        getActivity().startActivity(intent);
    }
}
