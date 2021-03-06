package com.cquant.lizone.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cquant.lizone.bean.PositionItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.CircleImageView;
import com.cquant.lizone.view.ItemDivider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by PC on 2015/9/11.
 */
public class PositionFragment extends  BaseFragment {

    private static final String TAG = " PositionFragment ";

    protected String url = Utils.BASE_URL+"Chicang_list/";

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
        getMaster();
        Log.d("TianjunXu", "SignMasterFragment:onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("TianjunXu", "SignMasterFragment:onPause");
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
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(),R.drawable.default_recylerview_divider));
    }

    private void initList() {
        mPositionList = new ArrayList<PositionItem>();
    }
    private void getMaster() {
        Log.d("TianjunXu", " getMaster:url = " + url);
        Request request = new Request.Builder().url(url).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    if ((obj != null) && (JsnTool.getInt(obj, "status") == 1)) {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_PARSE_DATA;
                        message.obj = msg;
                        message.sendToTarget();
                    }
                }
            }
        });
    }
    private static final int MSG_PARSE_DATA = 21;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_PARSE_DATA:{
                    parsePosition((String) msg.obj);
                    break;
                }
                default:break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }
    private void parsePosition(String msg) {
        mPositionList =PositionItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
    }

    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sign_master_list_item,viewGroup,false);
            MasterViewHolder vh = new MasterViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            /*MasterViewHolder holder = (MasterViewHolder) viewHolder;
            holder.mTvName.setText(mMasterList.get(i).name);
            holder.mTvProfit.setText(mMasterList.get(i).yield+"%");
            holder.mTvNum.setText(mMasterList.get(i).num);
            holder.mTvSuccess.setText(mMasterList.get(i).success+"%");
            if(!TextUtils.isEmpty(mMasterList.get(i).photo)) {
                ImageLoader.getInstance().displayImage(mMasterList.get(i).photo,holder.mPhoto);
            }*/
        }

        @Override
        public int getItemCount() {
            return mPositionList.size();
        }

        class MasterViewHolder extends RecyclerView.ViewHolder{

            public CircleImageView mPhoto;
            public TextView mTvName;
            public TextView mTvProfit;
            public TextView mTvNum;
            public TextView mTvSuccess;

            public MasterViewHolder(View itemView) {
                super(itemView);
                mPhoto = (CircleImageView) itemView.findViewById(R.id.photo);

                mTvName = (TextView) itemView.findViewById(R.id.name);
                mTvNum = (TextView) itemView.findViewById(R.id.trade_num);
                mTvSuccess = (TextView) itemView.findViewById(R.id.success_ratio);
                mTvProfit  = (TextView) itemView.findViewById(R.id.profit_ratio);
            }
        }
    }
}
