package com.cquant.lizone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
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
 * Created by PC on 2015/8/26.
 */
public class RegisterActivity extends BaseActivity {

    private Toolbar toolbar;

    private WebHelper mWebhelper = null;

    private final int CHECK_USER = 1;
    private final int CHECK_PASSWORD = 2;
    private final int CHECK_CONFIRM = 4;
    private final int CHECK_NONE = 0;
    private boolean enable_user=true;
    private boolean hide_password = false;
    private int mVerifyCode = 1729;

    private int mCheckState = CHECK_NONE;
    private EditText mEtUser, mEtPassword;
    private EditText mEtVerify;
    private CheckBox mHidePass;
    private CheckBox mUserAgree;

    private Button mActionMenu;

    private View mPbProgress = null;
    private TextView mTvMsg = null;
    private String mStrMsg=null;

    private String url = Utils.BASE_URL+"Get_Reg/";
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
        setContentView(R.layout.register_activity_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionMenu = (Button)findViewById(R.id.actionbar_menu);
        mEtUser = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_phone);
        mEtVerify = (EditText) findViewById(R.id.et_verify);
        mHidePass = (CheckBox)findViewById(R.id.checkBox1);
        mUserAgree = (CheckBox)findViewById(R.id.check_user_agreement);
        mPbProgress = findViewById(R.id.pb_progress);
        mTvMsg = (TextView) findViewById(R.id.tv_msg);
        mPbProgress.setVisibility(View.GONE);
        mTvMsg.setText("");
        initToolBar();
        mHidePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                hide_password = isChecked;
                if (!hide_password) {
                    mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mEtPassword.setSelection(mEtPassword.getText().length());
                    mEtPassword.getPaint().setFakeBoldText(true);
                } else {
                    mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mEtPassword.setSelection(mEtPassword.getText().length());
                    mEtPassword.getPaint().setFakeBoldText(true);
                }
            }
        });
        mWebhelper = new WebHelper(this);
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.register);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mActionMenu.setText("登陆");
        mActionMenu.setVisibility(View.VISIBLE);
        mActionMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //打开登陆页
                loginResult();
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

    private void loginResult() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent, Utils.LOGIN_CODE);
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
    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.getVerify_btn:
                getVerifyCode();
                break;
            case R.id.submit: {
                if (enable_user&&checkSubmit()) {
                    startSubmitToServer();
                }
                break;
            }
            case R.id.text_user_agreement:
                openUserAgreement();
                break;
            default:
                break;
        }
    }
    private void openUserAgreement() {
        Intent intent = new Intent(this,WebPageActivity.class);
        intent.putExtra("title","使用协议");
        intent.putExtra("web_addr", Utils.BASE_URL+"xieyi/");
        startActivity(intent);
    }
    private void startSubmitToServer() {
        if(!mUserAgree.isChecked()){
            popMsg("您需要阅读并同意用户协议");
            return;
        }
        if(isNetAvailable()){
            enable_user=false;
            sendStateMsg("正在注册...", true);
            String params = "userid="+mEtUser.getText().toString().trim()+"&pwd="+mEtPassword.getText().toString().trim();
                //end add by TianjunXu
                mWebhelper.doLoadPost(url, params, new WebHelper.OnWebFinished() {
                    @Override
                    public void onWebFinished(boolean success, String msg) {
                        if (success) {
                            JSONObject response = JsnTool.getObject(msg);
                            if (response != null) {
                                if (parseRegisterResult(response)) {
                                    mWebhelper.cancleRequest();
                                    startLogin();
                                }
                            }
                                //LogTool.d(TAG+" login success:"+msg);
                            } else {
                                //tipsNetError("登陆失败，请重试");
                                popMsg("注册失败，请重试");
                            }
                            sendStateMsg("", false);
                            enable_user = true;
                        }
                    }

                    );
                }else{
            popMsg("网络不可用");
        }
    }

    private boolean parseRegisterResult(JSONObject response) {
        int status = JsnTool.getInt(response,"status");
        if(status == 1) {
            popMsg("注册成功");
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_USER_ID, mEtUser.getText().toString().trim());
            return true;
        } else if(status == 0) {
            popMsg(JsnTool.getString(response, "msg"));
            return false;
        } else {
            popMsg("注册失败，请重试");
            return false;
        }
    }

    private void getVerifyCode() {
        if (!(checkCellPhone(mEtUser))) {
            return;
        }
        String url = Utils.BASE_URL+Utils.GET_CODE_ADDR+Utils.PARA_USER_ID+"/"+mEtUser.getText().toString().trim();
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                if (success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if (response != null) {
                        if (parseCodeResult(response)) {
                            mWebhelper.cancleRequest();
                        }
                    } else {
                        //tipsNetError(getString(R.string.get_code_error)+","+getString(R.string.check_net));
                        popMsg(getString(R.string.get_code_error) + "," + getString(R.string.check_net));
                    }

                } else {
                    //tipsNetError(getString(R.string.get_code_error)+","+getString(R.string.check_net));
                    popMsg(getString(R.string.get_code_error) + "," + getString(R.string.check_net));
                }
            }
        });
    }
    private void startLogin() {
        if (isNetAvailable()) {
            enable_user = false;
            sendStateMsg("正在登录...", true);
            String params = "name="+mEtUser.getText().toString().trim()+"&pwd="+mEtPassword.getText().toString().trim();
            mWebhelper.doLoadPost(Utils.BASE_URL+Utils.LOGIN_ADDR, params,
                    new WebHelper.OnWebFinished() {
                        @Override
                        public void onWebFinished(boolean success,
                                                  String msg) {
                            if (success) {
                                parseAccountInf(msg);
                                mWebhelper.cancleRequest();
                                //LogTool.d(TAG+" login success:"+msg);
                            } else {
                                tipsNetError("登陆失败，请重新登陆");
                            }
                            sendStateMsg("", false);
                            enable_user = true;
                        }
                    });
        } else {
            tipsNetError("网络不可用，登陆失败");
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
                    tipsNetError(str + ",请重新登陆");
                } else {
                    tipsNetError("登录出错了，请重新登陆");
                }
            }
        } else {
            tipsNetError("登录出错了，请重新登陆");
        }
    }
    private  void saveAccount() {
        if(!LizoneApp.mCanLogin ) {
            LizoneApp.mCanLogin = true;
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_USER_ID, mEtUser.getText().toString().trim());
            SharedPrefsUtil.putStringValue(LizoneApp.getApp(), SharedPrefsUtil.PREFS_PASS, mEtPassword.getText().toString().trim());
        }
    }
    private boolean parseCodeResult(JSONObject response) {
        int status = JsnTool.getInt(response,"status");
        if(status == 1) {
            mVerifyCode = JsnTool.getInt(response,"data");
            Toast.makeText(this, JsnTool.getString(response,"msg"),
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if(status == 0) {
            popMsg(JsnTool.getString(response, "msg"));
            return false;
        } else {
            popMsg(getString(R.string.get_code_error));
            return false;
        }
    }

    private void tipsNetError(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setNegativeButton(R.string.ok_msg,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dissmiss
                        loginResult();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Utils.LOGIN_CODE) {
            finish();
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
        if (mCheckState != 7) {
            int check = 7 - mCheckState;
            int adjust = 0x1;
            while ((check & adjust) != adjust) {
                adjust <<= 1;
            }
            if (adjust == CHECK_USER) {
                if(checkCellPhone(mEtUser)){
                    checkSubmit();
                }
            } else if (adjust == CHECK_PASSWORD) {
                if(checkPassword(mEtPassword)){
                    checkSubmit();
                }
            } else {
                addFlag(CHECK_CONFIRM);
            }
            return mCheckState == 7;
        }
        if(!checkVerifyCode()) {
            return false;
        }
        return true;
    }
    private boolean checkVerifyCode() {
        if(mEtVerify.getText().toString().trim().equals(mVerifyCode+"")
                || mEtVerify.getText().toString().trim().equals(Utils.SUPER_CODE+"")) {
            return true;
        } else {
            popMsg("请输入正确的验证码");
            return false;
        }
    }
}
