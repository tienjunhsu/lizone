package com.cquant.lizone.frag;

import android.content.Intent;
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
import com.cquant.lizone.activity.NewsPageActivity;
import com.cquant.lizone.bean.DynamicItem;
import com.cquant.lizone.bean.NewsItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.FullyLinearLayoutManager;
import com.cquant.lizone.view.ItemDivider;
import com.cquant.lizone.view.OnItemClickListener;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class NewsFragment  extends BaseFragment{
    private static final String NewsFragment = "DynamicFragment:Content";

    private String mContent ;

    private String url;
    private WebHelper mWebhelper = null;

    private ACache mACache;

    private ArrayList<NewsItem> mNewsList;
    private String mFileName;

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;

    private int type;

    public static NewsFragment newInstance(String content) {
        NewsFragment fragment = new NewsFragment();


        fragment.mContent = content;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = Utils.BASE_URL + mContent;
        parseType();
        mFileName = Md5FileNameGenerator.generate(url);
    }

    private void parseType() {
        if(mContent.startsWith("Report")) {
            type = 2;
        }else if(mContent.contains("Article/type/1")) {
            type = 0;
        } else if(mContent.contains("Article/type/2")) {
            type = 1;
        }
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
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(), R.drawable.default_recylerview_divider));
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        LogTool.v("start:url = "+url+",mContent="+mContent);
        //getDynamic();
    }
    @Override
    public void onResume() {
        super.onResume();
        getNews();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    private void initList() {
        String list_str = mACache.getAsString(mFileName);
        if( list_str !=null) {
            mNewsList=NewsItem.getItemList(list_str);
        } else {
            mNewsList = new ArrayList<NewsItem>();
        }
    }
    private void initReRecyclerView() {
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NewsAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void parseNews(String msg) {
        mNewsList =NewsItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
    }
    private void getNews() {
        LogTool.v("getNews:url = " + url);
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.v( " getNews,onWebFinished:success = " + success + ",msg =" + msg);
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parseNews(msg);
                    }
                }
            }
        });
    }
    private class NewsAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item,parent,false);
            NewsViewHolder vh = new NewsViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            NewsViewHolder mHolder = (NewsViewHolder) holder;
            mHolder.mTvTitle.setText(mNewsList.get(position).title);
            mHolder.mTvInfo.setText(" \u3000"+mNewsList.get(position).info);
            mHolder.mTvTime.setText(mNewsList.get(position).time);
        }

        @Override
        public int getItemCount() {
            return mNewsList.size();
        }
    }
    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTvTime;
        public TextView mTvInfo;
        public TextView mTvTitle;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onClick(view,getPosition());
        }
    }
    private OnItemClickListener mOnClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v,int position) {
           startActivity(position);
        }
    };

    private void startActivity(int position) {
        Intent intent = new Intent(getActivity(), NewsPageActivity.class);
        if(type == 2) {
            intent.putExtra("title","策略研报");
            intent.putExtra("web_addr","ReportWeb/tid/"+mNewsList.get(position).id);
        } else {
            intent.putExtra("web_addr","artweb/tid/"+mNewsList.get(position).id);
            if(type == 0) {
                intent.putExtra("title", "财经热点");
            } else {
                intent.putExtra("title", "市场要闻");
            }
        }
        getActivity().startActivity(intent);
    }
}
