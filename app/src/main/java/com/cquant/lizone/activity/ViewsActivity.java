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
import com.cquant.lizone.net.LoginWatcher;
import com.cquant.lizone.net.WebHelper;
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
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private ViewsAdapter mAdapter;

    private ArrayList<ViewsItem> mViewsList;

    private int longViewsNum =0;//看多人数
    private int shortViewsNum= 0;//看空人数

    private int type;//0,交易观点；1，交易

    private String url;

    private TextView mTvLongNum;
    private TextView mTvShortNum;

    private ProgressBar mProgress;

	private EditText mEditMsg;

    OkHttpClient client = new OkHttpClient();

    private void setSession() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
    }

    private void okLogin() {
        String  paras = LoginWatcher.getLoginStr();
        RequestBody body = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"),paras);
        Request request = new Request.Builder().url(Utils.BASE_URL + "Login/").post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                LogTool.e("okLogin:onFailure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                LogTool.e("okLogin:response");
            }
        });


    }
    private void addAgree_ok(int position) {
        if(!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        String paras = "id/"+mViewsList.get(position).viewid;
        final int pos =position;
        Request request = new Request.Builder().url(Utils.BASE_URL + "add_zan/" + paras).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String log ="";
                try{
                    log = log+request.body().toString();
                } catch (Exception ex) {
                    LogTool.e("addAgree_ok:my_ex1"+ex.getMessage());
                }
                try{
                    log = log+e.getMessage();
                } catch (Exception ex2) {
                    LogTool.e("addAgree_ok:my_ex"+ex2.getMessage());
                }
                LogTool.e("addAgree_ok:onFailure->log="+log);
                Message message = mHanler.obtainMessage();
                message.what = MSG_POP_MSG;
                message.obj = "点赞失败";
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String msg = response.body().string();
                LogTool.e("addAgree_ok:onResponse->msg="+msg);
            }
        });
        /*mWebHelper.doLoadGet(Utils.BASE_URL + "add_zan/" + paras, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.e("addAgree:msg ="+msg);
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHanler.obtainMessage();
                            message.what = MSG_REFRESH_AGREE_VIEW;
                            message.obj = pos;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //mWebHelper.cancleRequest();
                }
            }
        });*/
    }


	private static final int MSG_GETVIEWS = 1;
	private static final int MSG_REFRESH_NUMVIEW = 2;
	private static final int MSG_REVERSE_FOCUS  = 3;
    private static final int MSG_REFRESH_AGREE_VIEW =4;
    private static final int MSG_POP_MSG =5;


	private Handler mHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_GETVIEWS) {  //也许重构成switch语句更好吧
                getViews();
                mEditMsg.setText("");//观点发送成功后把编辑框内的删除掉
            } else if (msg.what == MSG_REFRESH_NUMVIEW) {
                //JSONObject data = (JSONObject)msg.obj;
               refreshNumView();

            }else if(msg.what==MSG_REVERSE_FOCUS){
                reverseFocus((int)msg.obj);
            } else if(msg.what==MSG_REFRESH_AGREE_VIEW) {
			    refreshAgreeView((int)msg.obj);
			} else if(msg.what==MSG_POP_MSG) {
                popMsg((String)msg.obj);
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        label = getIntent().getStringExtra("label");
        type = getIntent().getIntExtra("type",0);

        setContentView(R.layout.views_activity);

        if(type == 1) {
            findViewById(R.id.ly_bottom).setVisibility(View.GONE);//交易的时候把底部的EditText隐藏
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        //mRecyclerView.addItemDecoration(new ItemDivider(this, R.drawable.event_list_divider));

		mEditMsg = (EditText)findViewById(R.id.edit_txt);

        if(type == 0) {
            url = Utils.BASE_URL + "Guand_list/label/";
        } else {
            url = Utils.BASE_URL + "Jiaoyi_list/label/";
        }

        initToolBar();
        mWebHelper = new WebHelper(this);
        mFileName = Md5FileNameGenerator.generate(url);
        mACache = LizoneApp.getACache();

        setSession();//hsu
        okLogin();//hsu

        initList();
        initReRecyclerView();

        setRecylcerViewHeader();
    }

    private void setRecylcerViewHeader() {

        RecyclerViewHeader header = RecyclerViewHeader.fromXml(this, R.layout.view_activity_header);

        mTvLongNum = (TextView) header.findViewById(R.id.tv_num_long);
        mTvShortNum  = (TextView)header.findViewById(R.id.tv_num_short);

        mProgress = (ProgressBar)header.findViewById(R.id.progress);
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
        if( list_str !=null) {
            mViewsList =ViewsItem.getItemList(list_str);
        } else {
            mViewsList = new ArrayList<ViewsItem>();
        }
    }
    private void getViews() {
        if(!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        mWebHelper.doLoadGet(url + label, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    if ((obj != null) && (JsnTool.getInt(obj, "status") == 1)) {
                        parseViews(msg);
                    }
                    //mWebHelper.cancleRequest();
                }
            }
        });
    }

    private void parseViews(String msg) {
        mViewsList =ViewsItem.getItemList(msg);
        mAdapter.notifyDataSetChanged();
        mACache.put(mFileName, msg);
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

                            //刷新界面
                            Message message = mHanler.obtainMessage();
                            message.what = MSG_REFRESH_NUMVIEW;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //mWebHelper.cancleRequest();
                }
            }
        });
    }

   private void refreshNumView() {
       mTvLongNum.setText(longViewsNum+"");
       mTvShortNum.setText(shortViewsNum+"");
       mProgress.setProgress(longViewsNum * 100 / (longViewsNum + shortViewsNum));
   }
    private void addOrDelFocus(int position) {
        if(!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
		final int pos = position;
		String paras = "uid/"+mViewsList.get(position).userid;
        mWebHelper.doLoadGet(Utils.BASE_URL + "add_guanzhu/"+ paras,null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.e("addOrDelFocus:msg ="+msg);
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if (obj != null) {
                            int status = obj.getInt("status");
                            String tipsMsg = obj.getString("msg");
                            if (StrTool.areNotEmpty(tipsMsg)) {
                                popMsg(tipsMsg);
                            }
                            if (obj.getInt("status") == 1) {
                                Message message = mHanler.obtainMessage();
                                message.what = MSG_REVERSE_FOCUS;
                                message.obj =pos;
                                message.sendToTarget();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //mWebHelper.cancleRequest();
                }
            }
        });
    }

	private void addMessage() {
        if(!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
        String paras = "label/"+label+"/text/"+mEditMsg.getText().toString();
        LogTool.e("addMessage:paras =" + paras);
        mWebHelper.doLoadGet(Utils.BASE_URL + "add_guand/" + paras, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.d("addMessage:msg =" + msg);
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if (obj != null) {
                            int status = obj.getInt("status");
                            String tipsMsg = obj.getString("msg");
                            if (StrTool.areNotEmpty(tipsMsg)) {
                                popMsg(tipsMsg);
                            }
                            if (obj.getInt("status") == 1) {
                                Message message = mHanler.obtainMessage();
                                message.what = MSG_GETVIEWS;
                                message.sendToTarget();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        popMsg("添加观点失败");
                    }
                    //mWebHelper.cancleRequest();
                } else {
                    popMsg("添加观点失败");
                }
             }
         });
	}

    private void checkAndSubView() {
		if(StrTool.areEmpty(mEditMsg.getText().toString())) {
            popMsg("观点不能为空");
			return;
		}  else {
            addMessage();
		}
	}
    private void reverseFocus(int pos) {
		if(mViewsList.get(pos).focus.equalsIgnoreCase("N")){
            mViewsList.get(pos).focus = "Y";
		} else {
            mViewsList.get(pos).focus = "N";
		}
        mAdapter.notifyDataSetChanged();
		//下面应该将界面滚到之前的位置
		//do
	}
    private void addAgree(int position) {
        if(!isNetAvailable()) {
            popMsg("连接不可用，请检查您的网络设置");
            return;
        }
		String paras = "id/"+mViewsList.get(position).viewid;
		final int pos =position;
        mWebHelper.doLoadGet(Utils.BASE_URL + "add_zan/" + paras, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                LogTool.e("addAgree:msg ="+msg);
                if (success) {
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHanler.obtainMessage();
                            message.what = MSG_REFRESH_AGREE_VIEW;
                            message.obj = pos;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //mWebHelper.cancleRequest();
                }
            }
        });
    }
	
	private void refreshAgreeView(int position) {
        mViewsList.get(position).agree_num = (StrTool.getInt(mViewsList.get(position).agree_num)+1)+"";
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
        mWebHelper.cancleRequest();
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
            final ViewsViewHolder holder = (ViewsViewHolder) viewHolder;
            ImageLoader.getInstance().displayImage(mViewsList.get(i).photo, holder.mImgHead);
             holder.mTvName.setText(mViewsList.get(i).name);
            holder.mTvTime.setText(mViewsList.get(i).time);
            if(mViewsList.get(i).focus.equalsIgnoreCase("N")){
                LogTool.e("focus not");
                holder.mImgFocus.setSelected(false);
            } else {
                LogTool.e("focus true:"+mViewsList.get(i).focus);
                holder.mImgFocus.setSelected(true);//add by hsu,2016/01/20
			}
            final int position =i;
            holder.mImgFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFocusClickListener.onClick(view,position);
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
            addAgree_ok(position);
        }
    };
    private OnItemClickListener mOnMessageClickListener = new OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            startDetailActivity(position);
        }
    };

    private void startDetailActivity(int position) {
        String mUrl = Utils.BASE_URL+"GuandXq/gid/"+mViewsList.get(position).viewid;
        Intent intent = new Intent(this,WebPageActivity.class);
        intent.putExtra("title","详情");
        intent.putExtra("web_addr",mUrl);
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
