package com.cquant.lizone.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.activity.WebPageActivity;
import com.cquant.lizone.bean.EventItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;
import com.cquant.lizone.view.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by asus on 2015/10/20.
 */
public class EventFragment extends BaseFragment {

    private static final String TAG = "EventFragment";

    protected String url = Utils.BASE_URL+"Myhuodong_list/sel_id/";
    private ACache mACache;

    private String mFileName;

    protected ArrayList<EventItem> mEventList;

    protected RecyclerView mRecyclerView;
    private MasterAdapter mAdapter;

    public static EventFragment newInstance(int taken) {
        EventFragment fragment = new EventFragment();


        fragment.taken = taken;

        return fragment;
    }
    private int taken;//0,全部活动;1,已参加活动

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = url+taken;
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
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(), R.drawable.event_list_divider));
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        //getDynamic();
    }
    @Override
    public void onResume() {
        super.onResume();
        getEvents();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                //相当于Fragment的onResume,当前可见
                LogTool.v(TAG + "setUserVisibleHint to true");

                getEvents();
            }
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(mFileName);
        super.onDestroy();
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
            mEventList =EventItem.getItemList(list_str);
        } else {
            mEventList = new ArrayList<EventItem>();
        }
    }
    private void getEvents() {
        Request request = new Request.Builder().url(url).tag(mFileName).build();
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
                    parseEvents((String) msg.obj);
                    break;
                }
                default:break;
            }
            super.handleMessage(msg);
        }
    };

    private void parseEvents(String msg) {
        mEventList =EventItem.getItemList(msg);
       mAdapter.notifyDataSetChanged();
    }
    public class MasterAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_list_item,viewGroup,false);
            EventViewHolder vh = new EventViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            EventViewHolder holder = (EventViewHolder) viewHolder;
            holder.mTvName.setText(mEventList.get(i).name);
            holder.mTvNum.setText(mEventList.get(i).num);
            holder.mTvTime.setText(mEventList.get(i).time);
            if(taken ==1) {
                holder.mTvStatus.setText("我已参加");
                holder.mTvStatus.setCompoundDrawablesRelative(null, null, null, null);
            } else {
                holder.mTvStatus.setText("立即参加");
                holder.mTvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.go), null);
            }
            ImageLoader.getInstance().displayImage(mEventList.get(i).img,holder.mImgView);

        }

        @Override
        public int getItemCount() {
            return mEventList.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView mTvName;//标题
            public ImageView mImgView;
            public TextView mTvNum;//已经参加人数
            public TextView mTvTime;//显示时间
            public TextView mTvStatus;//显示立即参加、我已参加

            public EventViewHolder(View itemView) {
                super(itemView);

                mTvName = (TextView) itemView.findViewById(R.id.tv_name);
                mTvNum = (TextView) itemView.findViewById(R.id.tv_num);
                mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
                mTvStatus  = (TextView) itemView.findViewById(R.id.tv_status);
                mImgView = (ImageView) itemView.findViewById(R.id.iv_img);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                mOnClickListener.onClick(view,getPosition());
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
        Intent intent = new Intent(getActivity(), WebPageActivity.class);
        intent.putExtra("title",mEventList.get(position).name);
        intent.putExtra("web_addr",mEventList.get(position).url);
        getActivity().startActivity(intent);
    }

}
