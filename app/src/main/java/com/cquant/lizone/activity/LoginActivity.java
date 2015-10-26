package com.cquant.lizone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.bean.AccountItem;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.SharedPrefsUtil;
import com.cquant.lizone.util.Utils;

import org.json.JSONObject;

/**
 * Created by asus on 2015/9/8.
 */
public class LoginActivity extends BaseActivity implements View.OnFocusChangeListener {

    private static final String TAG ="LoginActivity";

    private Toolbar toolbar;

    private final int CHECK_USER = 1;
    private final int CHECK_PASSWORD = 2;
    private final int CHECK_NONE = 0;

    private int mCheckState = CHECK_NONE;

    public boolean user_enable = true;

    private EditText mEtUser;
    private EditText mEtPassword;

    private WebHelper mWebhelper = null;

    private View mPbProgress = null;
    private TextView mTvMsg = null;
    private String mStrMsg=null;
    private Button mActionMenu;

    private String url = Utils.BASE_URL+"Login/";
    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:{
                    mTvMsg.setText(mStrMsg);
                    break;
                }
                case 1:{
                    mPbProgress.setVisibility(View.VISIBLE);
                    break;
                }
                case -1:{
                    mTvMsg.setText(mStrMsg);
                }
                case 2:{
                    mPbProgress.setVisibility(View.GONE);
                    break;
                }
                default:finish();
            }
        }
    };
    private void sendStateMsg(String str, boolean pbVisible) {
        mStrMsg = str;
        mHander.sendEmptyMessage(0);
        if (!pbVisible) {
            if(mPbProgress.getVisibility()==View.VISIBLE){
                mHander.sendEmptyMessage(2);
            }
        }else{
            if(mPbProgress.getVisibility()!=View.VISIBLE){
                mHander.sendEmptyMessage(1);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEtUser = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_phone);
        mPbProgress = findViewById(R.id.pb_progress);
        mTvMsg = (TextView) findViewById(R.id.tv_msg);
        mPbProgress.setVisibility(View.GONE);
        mTvMsg.setText("");
        mActionMenu = (Button)findViewById(R.id.actionbar_menu);
        initToolBar();
        mEtUser.setOnFocusChangeListener(this);
        mEtPassword.setOnFocusChangeListener(this);
        mWebhelper = new WebHelper(this);
        mEtUser.setText(SharedPrefsUtil.getStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_USER_ID, ""));
    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.login);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mActionMenu.setText("注册");
        mActionMenu.setVisibility(View.VISIBLE);
        mActionMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //打开注册页
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        super.initToolBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login: {
                if (user_enable && checkSubmit()) {
                    startSubmitToServer();
                }
                break;
            }
            default:
                break;
        }
    }

    private void tipsNetError(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setNegativeButton(R.string.ok_msg,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dissmiss
                    }
                })
                .setCancelable(false)
                .create()
                .show();

    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
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
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (v.equals(mEtUser)) {
                if (!StrTool.isEmpty(mEtUser.getText().toString())) {
                    checkCellPhone(mEtUser);
                }
            } else {
                if (!StrTool.isEmpty(mEtPassword.getText().toString())) {
                    checkPassword(mEtPassword);
                }
            }
        }
    }
    private boolean hasFlag(int flag) {
        return flag == (this.mCheckState & flag);
    }

    private void addFlag(int flag) {
        this.mCheckState |= flag;
    }

    private void subFlag(int flag) {
        if (hasFlag(flag)) {
            this.mCheckState ^= flag;
        }
    }
    private boolean checkPassword(EditText edit) {
        String name = edit.getText().toString().trim();
        if (name.equals("") || name.length() < 6) {
            subFlag(CHECK_PASSWORD);
            popMsg("密码不得小于6个字符");
            return false;
        } else {
            addFlag(CHECK_PASSWORD);
            return true;
        }
    }

    private boolean checkCellPhone(EditText edit) {
        if (!StrTool.isMobileNO(edit.getText().toString().trim())) {
            subFlag(CHECK_USER);
            popMsg("请输入正确的手机号");
            return false;
        } else {
            addFlag(CHECK_USER);
            return true;
        }
    }

    private boolean checkSubmit() {
        if (mCheckState != 3) {
            int check = 3 - mCheckState;
            int adjust = 0x1;
            while ((check & adjust) != adjust) {
                adjust <<= 1;
            }
            if (adjust == CHECK_USER) {
                if (checkCellPhone(mEtUser)) {
                    checkSubmit();
                }
            } else if (adjust == CHECK_PASSWORD) {
                checkPassword(mEtPassword);
            }
            return mCheckState == 3;
        }
        return true;
    }
    private void startSubmitToServer() {
        if (isNetAvailable()) {
            user_enable = false;
            sendStateMsg("正在登录...", true);
            String params = "name="+mEtUser.getText().toString().trim()+"&pwd="+mEtPassword.getText().toString().trim();
            mWebhelper.doLoadPost(url, params,
                    new WebHelper.OnWebFinished() {
                        @Override
                        public void onWebFinished(boolean success,
                                                  String msg) {
                            if (success) {
                                parseAccountInf(msg);
                                mWebhelper.cancleRequest();
                                //LogTool.d(TAG+" login success:"+msg);
                            } else {
                                //tipsNetError("登陆失败，请重试");
                                popMsg("登陆失败，请重试");
                            }
                            sendStateMsg("", false);
                            user_enable = true;
                        }
                    });
        } else {
            popMsg("网络不可用");
        }
    }
    private void parseAccountInf(String msg) {
        JSONObject json = JsnTool.getObject(msg);
        if(json != null) {
            if(JsnTool.getInt(json,"status")==1) {
                saveAccount();
                GlobalVar.sAccountInf = AccountItem.getItem(json);
                SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_ACCOUNT, msg);
                popMsg("登录成功");
                setResult(RESULT_OK, new Intent());
                finish();
            } else {
                String str=JsnTool.getString(json, "msg");
                if(StrTool.areNotEmpty(str)) {
                    popMsg(str);
                } else {
                    popMsg("登录出错了，稍后试试");
                }
            }
        } else {
            popMsg("登录出错了，稍后试试");
        }
    }
    private  void saveAccount() {
        if(!LizoneApp.mCanLogin ) {
            LizoneApp.mCanLogin = true;
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_USER_ID, mEtUser.getText().toString().trim());
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_PASS, mEtPassword.getText().toString().trim());
        }
    }
}
