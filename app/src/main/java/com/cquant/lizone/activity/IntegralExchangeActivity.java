package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.AccountOverViewItem;
import com.cquant.lizone.bean.GiftItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

/**
 * Created by asus on 2015/10/25.
 */
public class IntegralExchangeActivity extends  BaseActivity{

    private static final String TAG = "IntegralExchangeActivity";
    private Toolbar toolbar;

    private ACache mACache;

    private String url = Utils.BASE_URL+"gift_more/gid/";
    protected WebHelper mWebhelper = null;
    private String mFileName;

    private TextView mTvName;
    private TextView mTvPoint;
    private TextView mTvNumber;
    private TextView mTvPrice;
    private TextView mTvDesc;
    private ImageView mIvImg;
    private Button mBtnExchange;

    private GiftItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.integral_exchange_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        url = url+getIntent().getStringExtra("gid");
        initToolBar();
        mTvName = (TextView)findViewById(R.id.tv_name);
        mTvPoint = (TextView)findViewById(R.id.tv_point);
        mTvNumber = (TextView)findViewById(R.id.tv_number);
        mTvPrice = (TextView)findViewById(R.id.tv_price);
        mTvDesc = (TextView)findViewById(R.id.tv_desc);
        mIvImg = (ImageView)findViewById(R.id.iv_img);
        mBtnExchange = (Button)findViewById(R.id.btn_exchange);

        mWebhelper = new WebHelper(this);
        mACache = LizoneApp.getACache();
        mFileName = Md5FileNameGenerator.generate(url);
        getGift();
    }
    private void getGift() {
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parseGift(JsnTool.getObject(response, "data"));
                        mWebhelper.cancleRequest();
                    }
                }
            }
        });
    }

    private void parseGift(JSONObject data) {
        item = GiftItem.getItem(data);
        refreshView();
    }

    private void refreshView() {
        mTvName.setText(item.name);
        mTvPoint.setText(item.price);
        mTvPrice.setText(item.rmb);
        mTvNumber.setText(item.number);
        if(StrTool.getInt(GlobalVar.sAccountInf.integral)>=StrTool.getInt(item.price)) {
            mTvDesc.setText("可兑换");
            mBtnExchange.setText("立即兑换");
            mBtnExchange.setClickable(true);
        } else {
            mTvDesc.setText("积分不够？去赚积分");
            mBtnExchange.setText("去赚积分");
            mBtnExchange.setClickable(false);
        }

        ImageLoader.getInstance().displayImage(item.big_img, mIvImg);
    }
    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exchange:
                exchange();
                break;
            default:
                break;
        }
    }

    private void exchange() {
        mWebhelper.doLoadGet(Utils.BASE_URL+"/chushi", null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    LogTool.e("exchange:msg ="+msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        mWebhelper.cancleRequest();
                    }
                } else {
                    popMsg("兑换失败，请重试");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebhelper.cancleRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebhelper = null;
    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle("礼品兑换");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
