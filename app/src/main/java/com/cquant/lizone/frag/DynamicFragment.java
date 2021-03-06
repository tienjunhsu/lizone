package com.cquant.lizone.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.activity.LoginActivity;
import com.cquant.lizone.activity.OpenPositionActivity;
import com.cquant.lizone.bean.DynamicItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.ItemDivider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by PC on 2015/9/7.
 */
public class DynamicFragment extends BaseFragment {
    private static final String KEY_CONTENT = "DynamicFragment:Content";

	private static final String REGEX = "#.+#$";//匹配以#中新油100手#这样介绍的
	private Pattern mPattern = Pattern.compile(REGEX);

    public static DynamicFragment newInstance(String content) {
        DynamicFragment fragment = new DynamicFragment();


        fragment.mContent = content;

        return fragment;
    }

    private String mContent ;

	private String url = Utils.BASE_URL+Utils.SUB_LIST_ADDR+Utils.PARA_SUB_LIST;

	private ACache mACache;

	private ArrayList<DynamicItem> mDynamicList;
	private String mFileName;

    private RecyclerView mRecyclerView;
    private DynamicAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }//这里也许应该去掉，不必保存状态
		if(mContent.equals("master")) {
            url = url+"1/";
		}else {
            url = url+"0/";
		}
        mFileName = Md5FileNameGenerator.generate(url);
    }

    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
		mACache = LizoneApp.getACache();
        initList();
        initReRecyclerView();
   }

    private void initReRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new DynamicAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initList() {
        String list_str = mACache.getAsString(mFileName);
        if( list_str !=null) {
            mDynamicList =DynamicItem.getItemList(list_str);
        } else {
            mDynamicList = new ArrayList<DynamicItem>();
        }
    }
    private static final int MSG_PARSE_DATA = 21;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_PARSE_DATA:{
                    parseDynamic((String) msg.obj);
                    break;
                }
                default:break;
            }
            super.handleMessage(msg);
        }
    };
    private void getDynamic() {
        Log.d("TianjunXu"," getDynamic:url = "+url);
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

    private void parseDynamic(String msg) {
        mDynamicList =DynamicItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
    }
    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(mFileName);
        super.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dynamic_fragment, container, false);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(),R.drawable.dynamic_list_divider));
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_CONTENT, mContent);
    }

	  @Override
    public void onStart() {
          super.onStart();
          Log.d("TianjunXu","start:url = "+url+",mContent="+mContent);
          //getDynamic();
    }
    @Override
    public void onResume() {
        super.onResume();
        getDynamic();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    public class DynamicAdapter extends RecyclerView.Adapter {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dynamic_list_item,viewGroup,false);
            DynamicViewHolder vh = new DynamicViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            DynamicViewHolder holder = (DynamicViewHolder) viewHolder;
            holder.mTvName.setText(mDynamicList.get(i).name);
            holder.mTvTime.setText(mDynamicList.get(i).time);
            holder.mTvText.setText(mDynamicList.get(i).text);
            if(!TextUtils.isEmpty(mDynamicList.get(i).photo_url)) {
                ImageLoader.getInstance().displayImage(mDynamicList.get(i).photo_url,holder.mPhoto);
            }

			Matcher matcher = mPattern.matcher(mDynamicList.get(i).text.trim());
            //Log.d("temp","str ="+mDynamicList.get(i).text+",find ="+matcher.find());
			if(matcher.find()) {
                Log.d("temp","find");
                holder.mLyPick.setVisibility(View.VISIBLE);
                holder.mTvPickText.setText(matcher.group());
			} else {
                Log.d("temp","else");
                holder.mLyPick.setVisibility(View.GONE);
			}
            holder.mBtnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(LizoneApp.mCanLogin ) {
                        openPosition();
                    } else {
                        gotoLogin();
                    }
                }
            });

        }

        private void openPosition() {
            Intent intent = new Intent(getActivity(), OpenPositionActivity.class);
            getActivity().startActivity(intent);
        }
        private void gotoLogin() {
            getActivity().startActivity(new Intent(getActivity(),LoginActivity.class));
        }
        @Override
        public int getItemCount() {
            return mDynamicList.size();
        }

        class DynamicViewHolder extends RecyclerView.ViewHolder{

            public ImageView mPhoto;
            public TextView mTvTime;
            public TextView mTvName;
            public TextView mTvText;
            public TextView mTvPickText;
            public Button mBtnOrder;
            public RelativeLayout mLyPick;

            public DynamicViewHolder(View itemView) {
                super(itemView);
                mPhoto = (ImageView) itemView.findViewById(R.id.photo);
                mTvName = (TextView) itemView.findViewById(R.id.name);
                mTvTime = (TextView) itemView.findViewById(R.id.time);
                mTvText = (TextView) itemView.findViewById(R.id.text);
                mTvPickText= (TextView) itemView.findViewById(R.id.pick_text);
                mBtnOrder = (Button)itemView.findViewById(R.id.quick_order);
                mLyPick = (RelativeLayout)itemView.findViewById(R.id.ly_pick);
            }
        }
    }
}
