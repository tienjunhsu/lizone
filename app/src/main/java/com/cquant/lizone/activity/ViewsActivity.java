package com.cquant.lizone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.ViewsItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;
import com.cquant.lizone.view.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/30.
 */
public class ViewsActivity extends BaseActivity {
    private static final String TAG = "KPointActivity";

    private Toolbar toolbar;

    private WebHelper mWebHelper;
    private String label;//品种代码

    private ACache mACache;

    private String mFileName;

    private RecyclerView mRecyclerView;

    private ArrayList<ViewsItem> mViewsList;

    private int longViewsNum =0;//看多人数
    private int shortViewsNum= 0;//看空人数

    private int type;//0,交易观点；1，交易

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        label = getIntent().getStringExtra("label");
        type = getIntent().getIntExtra("type",0);

        setContentView(R.layout.views_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        //mRecyclerView.addItemDecoration(new ItemDivider(this, R.drawable.event_list_divider));

        initToolBar();
        mWebHelper = new WebHelper(this);
        //mFileName = Md5FileNameGenerator.generate(url);
        mACache = LizoneApp.getACache();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebHelper.cancleRequest();
    }
    @Override
    protected void initToolBar() {
		if(type == 0){
            toolbar.setTitle("讨论观点");
		} else {
            toolbar.setTitle("交易");
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
    private void getLongAndShortNum() {
        //获取多空观点数量
        mWebHelper.doLoadGet(Utils.BASE_URL + "duokong_sum/label/" + label, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            longViewsNum = Integer.parseInt(obj.getJSONObject("data").getString("kanduo"));
                            shortViewsNum = Integer.parseInt(obj.getJSONObject("data").getString("kankong"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mWebHelper.cancleRequest();
                }
            }
        });
    }

    private void addOrDelFocus(String uid) {
        mWebHelper.doLoadGet(Utils.BASE_URL + "add_guanzhu/uid/" + uid, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                           //Toast
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mWebHelper.cancleRequest();
                }
            }
        });
    }

    public void onXmlBtClick(View v) {

    }
    @Override
    public void onDestroy() {
        mWebHelper = null;
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
            ViewsViewHolder holder = (ViewsViewHolder) viewHolder;
            ImageLoader.getInstance().displayImage(mViewsList.get(i).photo, holder.mImgHead);
             holder.mTvName.setText(mViewsList.get(i).name);
            holder.mTvTime.setText(mViewsList.get(i).time);
            if(mViewsList.get(i).focus.equals("N")){
                holder.mImgFocus.setSelected(false);
            }
            final int position =i;
            holder.mImgFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFocusClickListener.onClick(view,position);
                }
            });
            holder.mTvText.setText(mViewsList.get(i).text);
            holder.mImgAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnAgreeClickListener.onClick(view, position);
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
            mImgAgree = (ImageView) itemView.findViewById(R.id.iv_img);
            mTvAgreeNum = (TextView) itemView.findViewById(R.id.tv_agree_num);
            mImgMessage = (ImageView) itemView.findViewById(R.id.img_message);
            mTvMessage = (TextView) itemView.findViewById(R.id.tv_message_num);
        }
    }

    private OnItemClickListener mOnAgreeClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
        }
    };
    private OnItemClickListener mOnMessageClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
        }
    };
    private OnItemClickListener mOnFocusClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            startActivity(position);
        }
    };

    private void startActivity(int position) {
        //Intent intent = new Intent(getActivity(), WebPageActivity.class);
        // intent.putExtra("title",mEventList.get(position).name);
        // intent.putExtra("web_addr",mEventList.get(position).url);
        // getActivity().startActivity(intent);
    }

}