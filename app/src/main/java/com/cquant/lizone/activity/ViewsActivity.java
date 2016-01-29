package com.cquant.lizone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.ViewsItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;
import com.cquant.lizone.view.OnItemClickListener;
import com.cquant.lizone.view.RecyclerViewHeader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by asus on 2015/10/30.
 */
public class ViewsActivity extends BaseActivity {
    private static final String TAG = "ViewsActivity";

    private Toolbar toolbar;

    private String label;//品种代码

    private ACache mACache;

    private String mFileName;

    private RecyclerView mRecyclerView;
    private ViewsAdapter mAdapter;

    private ArrayList<ViewsItem> mViewsList;

    private int longViewsNum = 0;//看多人数
    private int shortViewsNum = 0;//看空人数

    private int type;//0,交易观点；1，交易

    private String url;

    private TextView mTvLongNum;
    private TextView mTvShortNum;

    private ProgressBar mProgress;

    private EditText mEditMsg;

    private void addAgree(int position) {
        if (!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        String paras = "id/" + mViewsList.get(position).viewid;
        final int pos = position;
        Request request = new Request.Builder().url(Utils.BASE_URL + "add_zan/" + paras).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHanler.obtainMessage();
                message.what = MSG_POP_MSG;
                message.obj = "点赞失败";
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Message message = mHanler.obtainMessage();
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    LogTool.e("addAgree_ok:onResponse->msg=" + msg);
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            message.what = MSG_REFRESH_AGREE_VIEW;
                            message.obj = pos;
                        } else {
                            message.what = MSG_POP_MSG;
                            message.obj = "点赞失败";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        message.what = MSG_POP_MSG;
                        message.obj = "点赞失败";
                    }
                } else {
                    message.what = MSG_POP_MSG;
                    message.obj = "点赞失败";
                }
                message.sendToTarget();
            }
        });
    }


    private static final int MSG_GETVIEWS = 0;
    private static final int MSG_REFRESH_NUMVIEW = 1;
    private static final int MSG_REVERSE_FOCUS = 2;
    private static final int MSG_REFRESH_AGREE_VIEW = 3;
    private static final int MSG_POP_MSG = 4;
    private static final int MSG_PARSE_VIEW_DATA = 5;


    private Handler mHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GETVIEWS:
                    getViews();
                    mEditMsg.setText("");//观点发送成功后把编辑框内的删除掉
                    break;
                case MSG_REFRESH_NUMVIEW:
                    refreshNumView();
                    break;
                case MSG_REVERSE_FOCUS:
                    reverseFocus((int) msg.obj);
                    break;
                case MSG_REFRESH_AGREE_VIEW:
                    refreshAgreeView((int) msg.obj);
                    break;
                case MSG_POP_MSG:
                    popMsg((String) msg.obj);
                    break;
                case MSG_PARSE_VIEW_DATA:
                    parseViews((String) msg.obj);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        label = getIntent().getStringExtra("label");
        type = getIntent().getIntExtra("type", 0);

        setContentView(R.layout.views_activity);

        if (type == 1) {
            findViewById(R.id.ly_bottom).setVisibility(View.GONE);//交易的时候把底部的EditText隐藏
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //mRecyclerView.addItemDecoration(new ItemDivider(this, R.drawable.event_list_divider));

        mEditMsg = (EditText) findViewById(R.id.edit_txt);

        if (type == 0) {
            url = Utils.BASE_URL + "Guand_list/label/";
        } else {
            url = Utils.BASE_URL + "Jiaoyi_list/label/";
        }

        initToolBar();
        mFileName = Md5FileNameGenerator.generate(url);
        mACache = LizoneApp.getACache();

        initList();
        initReRecyclerView();

        setRecylcerViewHeader();
    }

    private void setRecylcerViewHeader() {

        RecyclerViewHeader header = RecyclerViewHeader.fromXml(this, R.layout.view_activity_header);

        mTvLongNum = (TextView) header.findViewById(R.id.tv_num_long);
        mTvShortNum = (TextView) header.findViewById(R.id.tv_num_short);

        mProgress = (ProgressBar) header.findViewById(R.id.progress);
        mProgress.setMax(100);

        header.attachTo(mRecyclerView);
    }

    private void initReRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new ItemDivider(this, R.drawable.views_list_divider));

        mAdapter = new ViewsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.addItemDecoration(new ItemDivider(this,R.drawable.default_recylerview_divider));
    }

    @Override
    public void onResume() {
        super.onResume();
        getViews();
        getLongAndShortNum();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void initToolBar() {
        if (type == 0) {
            toolbar.setTitle("讨论观点");
        } else {
            toolbar.setTitle("交易动态");
        }
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initList() {
        String list_str = mACache.getAsString(mFileName);
        if (list_str != null) {
            mViewsList = ViewsItem.getItemList(list_str);
        } else {
            mViewsList = new ArrayList<ViewsItem>();
        }
    }

    private void getViews() {
        if (!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        Request request = new Request.Builder().url(url + label).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    LogTool.e("addAgree_ok:onResponse->msg=" + msg);
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHanler.obtainMessage();
                            message.what = MSG_PARSE_VIEW_DATA;
                            message.obj = msg;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void parseViews(String msg) {
        mViewsList = ViewsItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
    }

    private void getLongAndShortNum() {
        //获取多空观点数量
        Request request = new Request.Builder().url(Utils.BASE_URL + "duokong_sum/label/" + label).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    LogTool.e("addAgree_ok:onResponse->msg=" + msg);
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            longViewsNum = Integer.parseInt(obj.getJSONObject("data").getString("kanduo"));
                            shortViewsNum = Integer.parseInt(obj.getJSONObject("data").getString("kankong"));

                            //刷新界面
                            Message message = mHanler.obtainMessage();
                            message.what = MSG_REFRESH_NUMVIEW;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void refreshNumView() {
        mTvLongNum.setText(longViewsNum + "");
        mTvShortNum.setText(shortViewsNum + "");
        mProgress.setProgress(longViewsNum * 100 / (longViewsNum + shortViewsNum));
    }

    private void addOrDelFocus(int position) {
        LogTool.d("addOrDelFocus:position ="+position);
        if (!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        final int pos = position;
        String paras = "uid/" + mViewsList.get(position).userid;
        Request request = new Request.Builder().url(Utils.BASE_URL + "add_guanzhu/" + paras).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHanler.obtainMessage();
                message.what = MSG_POP_MSG;
                message.obj = "添加关注失败";
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    LogTool.d("addOrDelFocus:msg ="+msg);
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if (obj != null) {
                            //int status = obj.getInt("status");
                            String tipsMsg = obj.getString("msg");
                            if (StrTool.areNotEmpty(tipsMsg)) {
                                Message message01 = mHanler.obtainMessage();
                                message01.what = MSG_POP_MSG;
                                message01.obj = tipsMsg;
                                message01.sendToTarget();
                            }
                            if (obj.getInt("status") == 1) {
                                Message message = mHanler.obtainMessage();
                                message.what = MSG_REVERSE_FOCUS;
                                message.obj = pos;
                                message.sendToTarget();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Message message02 = mHanler.obtainMessage();
                    message02.what = MSG_POP_MSG;
                    message02.obj = "添加关注失败";
                    message02.sendToTarget();
                }
            }
        });
    }

    private void addMessage() {
        if (!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        RequestBody body = new FormEncodingBuilder().add("label",label)
                .add("text",mEditMsg.getText().toString()).build();
        Request request = new Request.Builder().url(Utils.BASE_URL + "add_guand/").post(body).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHanler.obtainMessage();
                message.what = MSG_POP_MSG;
                message.obj = "添加观点失败";
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if (obj != null) {
                            int status = obj.getInt("status");
                            String tipsMsg = obj.getString("msg");
                            if (StrTool.areNotEmpty(tipsMsg)) {
                                Message message01 = mHanler.obtainMessage();
                                message01.what = MSG_POP_MSG;
                                message01.obj = tipsMsg;
                                message01.sendToTarget();
                            }
                            if (obj.getInt("status") == 1) {
                                Message message = mHanler.obtainMessage();
                                message.what = MSG_GETVIEWS;
                                message.sendToTarget();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message message03 = mHanler.obtainMessage();
                        message03.what = MSG_POP_MSG;
                        message03.obj = "添加观点失败";
                        message03.sendToTarget();
                    }
                } else {
                    Message message02 = mHanler.obtainMessage();
                    message02.what = MSG_POP_MSG;
                    message02.obj = "添加观点失败";
                    message02.sendToTarget();
                }
            }
        });
    }

    private void checkAndSubView() {
        if (StrTool.areEmpty(mEditMsg.getText().toString())) {
            popMsg("观点不能为空");
            return;
        } else {
            addMessage();
        }
    }

    private void reverseFocus(int pos) {
        LogTool.d("reverseFocus:pos ="+pos);
        if (mViewsList.get(pos).focus.equalsIgnoreCase("N")) {
            mViewsList.get(pos).focus = "Y";
        } else {
            mViewsList.get(pos).focus = "N";
        }
        mAdapter.notifyDataSetChanged();
        //下面应该将界面滚到之前的位置
        //do
    }

    private void refreshAgreeView(int position) {
        mViewsList.get(position).agree_num = (StrTool.getInt(mViewsList.get(position).agree_num) + 1) + "";
        mAdapter.notifyDataSetChanged();
        //下面应该将界面滚到之前的位置
        //do
    }

    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.img_long:
                agreeWithLong();
                break;
            case R.id.img_short:
                agreeWithShort();
                break;
            case R.id.submit:
                checkAndSubView();
                break;
            default:
                break;
        }

    }

    private void agreeWithShort() {
        shortViewsNum++;
        //update to net
        refreshNumView();
    }

    private void agreeWithLong() {
        longViewsNum++;
        //update to net
        refreshNumView();
    }

    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }

    public class ViewsAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.views_list_item, viewGroup, false);
            ViewsViewHolder vh = new ViewsViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            final ViewsViewHolder holder = (ViewsViewHolder) viewHolder;
            ImageLoader.getInstance().displayImage(mViewsList.get(i).photo, holder.mImgHead);
            holder.mTvName.setText(mViewsList.get(i).name);
            holder.mTvTime.setText(mViewsList.get(i).time);
            if (mViewsList.get(i).focus.equalsIgnoreCase("N")) {
                LogTool.e("focus not");
                holder.mImgFocus.setSelected(false);
            } else {
                LogTool.e("focus true:" + mViewsList.get(i).focus);
                holder.mImgFocus.setSelected(true);//add by hsu,2016/01/20
            }
            final int position = i;
            holder.mImgFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFocusClickListener.onClick(view, position);
                    /*if(mViewsList.get(position).focus.equals("N")){
                        mViewsList.get(position).focus = "Y";
                        holder.mImgFocus.setSelected(true);
                    } else {
                        mViewsList.get(position).focus = "N";
                        holder.mImgFocus.setSelected(false);
                    }*/
                    //mod by hsu，2016/01/20，界面更新放在后面去
                }
            });
            holder.mTvText.setText(mViewsList.get(i).text);
            holder.mImgAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnAgreeClickListener.onClick(view, position);
                    //mViewsList.get(position).agree_num = (Integer.getInteger(mViewsList.get(position).agree_num,0)+1)+"";
                    //holder.mTvAgreeNum.setText(mViewsList.get(position).agree_num);//hsu,2016/01/20
                }
            });
            holder.mTvAgreeNum.setText(mViewsList.get(i).agree_num);
            holder.mImgMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnMessageClickListener.onClick(view, position);
                }
            });
            holder.mTvMessage.setText(mViewsList.get(i).discuss_num);

        }

        @Override
        public int getItemCount() {
            return mViewsList.size();
        }
    }

    class ViewsViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImgHead;
        public TextView mTvName;
        public TextView mTvTime;//显示时间
        public ImageView mImgFocus;
        public TextView mTvText;
        public ImageView mImgAgree;
        public TextView mTvAgreeNum;
        public ImageView mImgMessage;
        public TextView mTvMessage;

        public ViewsViewHolder(View itemView) {
            super(itemView);

            mImgHead = (ImageView) itemView.findViewById(R.id.photo);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mImgFocus = (ImageView) itemView.findViewById(R.id.img_focus);
            mTvText = (TextView) itemView.findViewById(R.id.tv_text);
            mImgAgree = (ImageView) itemView.findViewById(R.id.img_vote);
            mTvAgreeNum = (TextView) itemView.findViewById(R.id.tv_agree_num);
            mImgMessage = (ImageView) itemView.findViewById(R.id.img_message);
            mTvMessage = (TextView) itemView.findViewById(R.id.tv_message_num);
        }
    }

    private OnItemClickListener mOnAgreeClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            addAgree(position);
        }
    };
    private OnItemClickListener mOnMessageClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            startDetailActivity(position);
        }
    };

    private void startDetailActivity(int position) {
        String mUrl = Utils.BASE_URL + "GuandXq/gid/" + mViewsList.get(position).viewid;
        Intent intent = new Intent(this, WebPageActivity.class);
        intent.putExtra("title", "详情");
        intent.putExtra("web_addr", mUrl);
        startActivity(intent);
    }

    private OnItemClickListener mOnFocusClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            addOrDelFocus(position);
        }
    };

    private void startActivity(int position) {
        //Intent intent = new Intent(getActivity(), WebPageActivity.class);
        // intent.putExtra("title",mEventList.get(position).name);
        // intent.putExtra("web_addr",mEventList.get(position).url);
        // getActivity().startActivity(intent);
    }

}
