package com.cquant.lizone.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.activity.AccountActivity;
import com.cquant.lizone.activity.OpenPositionActivity;
import com.cquant.lizone.bean.AccountOverViewItem;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountOverviewFragment extends BaseFragment {

    private static final String TAG = "AccountOverviewFragment";

    private ACache mACache;

    private String url = Utils.BASE_URL+"MyAccount/";

    private TextView mTvFloatingProfit;
    private ImageView mIconFloating;

    private TextView mTotalCapital;
    private TextView mOccupyCash;
    private TextView mFollowCapital;
    private TextView mPositionCapital;
    private TextView mAvailableCash;
    private TextView mRiskRatio;

    private Button mBtnOpenPosition;
    private Button mBtnClosePosition;

    private AccountOverViewItem mOverView;
    private String mFileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mACache = LizoneApp.getACache();
        mFileName = Md5FileNameGenerator.generate(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.account_overview_fragment, container, false);
        mTvFloatingProfit = (TextView)root.findViewById(R.id.floating_profit);
        mIconFloating = (ImageView)root.findViewById(R.id.icon_floating);

        mTotalCapital = (TextView)root.findViewById(R.id.tv_total_capital);
        mOccupyCash = (TextView)root.findViewById(R.id.tv_occupy_cash);
        mFollowCapital = (TextView)root.findViewById(R.id.tv_follow_capital);
        mPositionCapital = (TextView)root.findViewById(R.id.tv_position_capital);
        mAvailableCash = (TextView)root.findViewById(R.id.tv_available_cash);
        mRiskRatio = (TextView)root.findViewById(R.id.tv_risk_ratio);

        mBtnOpenPosition = (Button)root.findViewById(R.id.btn_open_position);
        mBtnClosePosition = (Button)root.findViewById(R.id.btn_close_position);

        mBtnOpenPosition.setOnClickListener(mOpenPositionListener);
        mBtnClosePosition.setOnClickListener(mClosePositionListener);
        return root;
    }
    private View.OnClickListener mOpenPositionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            openPosition();
        }
    };
    private View.OnClickListener mClosePositionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            closePosition();
        }
    };

    private void openPosition() {
          Intent intent = new Intent(getActivity(), OpenPositionActivity.class);
         getActivity().startActivity(intent);
    }
    private void closePosition() {
        ((AccountActivity)getActivity()).setCurrentItem(1);//进入持仓页
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        getOverView();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initOverViewData();
    }
    private void initOverViewData(){
        String str = mACache.getAsString(mFileName);
        if(str != null) {
            mOverView = AccountOverViewItem.getItem(mACache.getAsString(mFileName));
            refreshOverView();
        }
    }
    private void refreshOverView() {
        Log.d("TianjunXu","refreshOverView():="+mOverView.floating+",total_capital ="+mOverView.total_capital);
        mTvFloatingProfit.setText(mOverView.floating+"");
        mTotalCapital.setText(mOverView.total_capital+"");
        mOccupyCash.setText(mOverView.use_cash+"");
        mFollowCapital.setText(mOverView.follow_cash+"");
        mPositionCapital.setText(mOverView.position_capital+"");
        mAvailableCash.setText(mOverView.available_capital+"");
        mRiskRatio.setText(mOverView.risk_rate);

        //if(StrTool.getDouble(mOverView.floating) < 0) {
        if(mOverView.floating < 0) {
            mIconFloating.setImageResource(R.drawable.icon_floating_down);
            mTvFloatingProfit.setTextColor(getActivity().getResources().getColor(R.color.green_two));
        } else {
            mIconFloating.setImageResource(R.drawable.icon_floating_up);
            mTvFloatingProfit.setTextColor(getActivity().getResources().getColor(R.color.red_two));
        }


    }
    private void getOverView() {
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
                    parseOverView((String) msg.obj);
                    break;
                }
                default:break;
            }
            super.handleMessage(msg);
        }
    };
    private void parseOverView(String msg) {

        mOverView =AccountOverViewItem.getItem(msg);
        refreshOverView();
        mACache.put(mFileName, msg);
    }

    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }
}
