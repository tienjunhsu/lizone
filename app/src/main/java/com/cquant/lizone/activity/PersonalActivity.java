package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.AccountOverViewItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.ACache;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.Md5FileNameGenerator;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

/**
 * Created by asus on 2015/10/23.
 */
public class PersonalActivity extends BaseActivity {

    private CircleImageView mImgHead;
    private EditText mEditName;
    private TextView mTvSign;
    private TextView mTvTotalCapital;
    private TextView mTvAvailableCash;
    private TextView mTvRisk;

    private Toolbar toolbar;

    private ACache mACache;

    private String url = Utils.BASE_URL+"MyAccount/";
    protected WebHelper mWebhelper = null;
    private AccountOverViewItem mOverView;
    private String mFileName;
    private View.OnFocusChangeListener mEditNameFocusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(!b) {
                checkUserName();
            }
        }
    };

    private void checkUserName() {
        if(mEditName.getText().toString().trim().isEmpty()) {
            popMsg("用户名不能为空");
            mEditName.setText(GlobalVar.sAccountInf.name);
            return;
        } else if(mEditName.getText().toString().trim().equals(GlobalVar.sAccountInf.name)) {
            return;
        } else {
            updateUserName();
        }
    }

    private void updateUserName() {
        //上传用户名，post
       String name_url = Utils.BASE_URL+"EditName/";
        String paras = "name="+mEditName.getText().toString().trim();
        mWebhelper.doLoadPost(name_url, paras, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                if(success) {
                    JSONObject json = JsnTool.getObject(msg);
                    if((json != null)&&(JsnTool.getInt(json,"status")==1)) {
                        popMsg("更改成功");
                        mWebhelper.cancleRequest();
                        GlobalVar.sAccountInf.name = mEditName.getText().toString().trim();
                    } else {
                        popMsg("更改失败，请重试");
                        mEditName.setText(GlobalVar.sAccountInf.name);
                    }
                } else {
                    popMsg("更改失败，请重试");
                    mEditName.setText(GlobalVar.sAccountInf.name);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mImgHead = (CircleImageView)findViewById(R.id.iv_portrait);
        mEditName = (EditText)findViewById(R.id.tv_name);
        mEditName.setOnFocusChangeListener(mEditNameFocusChanged);
        mTvSign = (TextView)findViewById(R.id.personal_sign);
        mTvTotalCapital = (TextView)findViewById(R.id.tv_total_capital);
        mTvAvailableCash = (TextView)findViewById(R.id.tv_available_cash);
        mTvRisk = (TextView)findViewById(R.id.tv_risk_ratio);
        initToolBar();
        mWebhelper = new WebHelper(this);
        mACache = LizoneApp.getACache();
        mFileName = Md5FileNameGenerator.generate(url);
        initOverViewData();
    }
    private void initOverViewData(){
        String str = mACache.getAsString(mFileName);
        if(str != null) {
            mOverView = AccountOverViewItem.getItem(mACache.getAsString(mFileName));
            refreshInfo();
        }
    }
    private void refreshInfo() {
        ImageLoader.getInstance().displayImage(GlobalVar.sAccountInf.photo, mImgHead);
        mEditName.setText(GlobalVar.sAccountInf.name);
        mTvSign.setText(GlobalVar.sAccountInf.profile);
        mTvTotalCapital.setText( "当前资产:"+mOverView.total_capital);
        mTvAvailableCash.setText("可用资产:"+mOverView.available_capital);
        mTvRisk.setText("账户风险率:"+mOverView.risk_rate);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getOverView();
    }

    @Override
    protected void onPause() {
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
        toolbar.setTitle("个人中心");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getOverView() {
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        parseOverView(msg);
                        mWebhelper.cancleRequest();
                    }
                }
            }
        });
    }
    private void parseOverView(String msg) {

        mOverView =AccountOverViewItem.getItem(msg);
        refreshInfo();
        mACache.put(mFileName, msg);
    }
    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.ly_sign:
                break;
            case R.id.log_out:
                logout();
                break;
            default:
                break;
        }
    }

    private void logout() {
        GlobalVar.sAccountInf = null;
        GlobalVar.SESSIONID = null;
        //SharedPrefsUtil.remove(LizoneApp.getApp(),SharedPrefsUtil.PREFS_USER_ID);
        SharedPrefsUtil.remove(LizoneApp.getApp(),SharedPrefsUtil.PREFS_PASS);
        LizoneApp.mCanLogin = false;
        clearAccountCache();
        finish();
    }
    private void clearAccountCache(){
        mACache.remove(mFileName);
        mACache.remove(Md5FileNameGenerator.generate(Utils.BASE_URL+Utils.SUB_LIST_ADDR+Utils.PARA_SUB_LIST+"0/"));
        mACache.remove(Md5FileNameGenerator.generate(Utils.BASE_URL+"MyAccount/"));
        //mACache.remove(Md5FileNameGenerator.generate(Utils.BASE_URL+"Chicang_list/"));
        mACache.remove(Md5FileNameGenerator.generate(Utils.BASE_URL+"DealList/"));
    }
}
