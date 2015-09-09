package com.cquant.lizone.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.tool.ACache;

/**
 * Created by PC on 2015/9/9.
 */
public class AccountOverviewFragment extends BaseFragment {

    private static final String TAG = "AccountOverviewFragment";

    private ACache mACache;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mACache = LizoneApp.getACache();
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
        mBtnClosePosition = (Button)root.findViewById(R.id.btn_open_position);

        //mBtnOpenPosition.setOnClickListener();

        return root;
    }
    View.OnClickListener mOpenPositionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            openPosition();
        }
    };

    private void openPosition() {

    }

    View.OnClickListener mClosePositionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            closePosition();
        }
    };

    private void closePosition() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
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
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
