package com.cquant.lizone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.tool.AppTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.Utils;

/**
 * Created by asus on 2015/10/23.
 */
public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    private Toolbar toolbar;
    private TextView mTvVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvVersion = (TextView)findViewById(R.id.tv_version);
        mTvVersion.setText("V" + AppTool.getVersionName(LizoneApp.getApp()));
        initToolBar();
    }
    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.setting);
        toolbar.setNavigationIcon(R.drawable.ic_back);
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
            case R.id.personal_center:
                openPersonalCenter();
                break;
            case R.id.open_firm:
                openFirmAccount();
                break;
            case R.id.service_call:
                callService();
                break;
            case R.id.use_agreement:
                openUserAgreement();
                break;
            case R.id.about_us:
                openAboutUs();
                break;
            case R.id.check_version:
                checkVersion();
                break;
            default:
                break;
        }
    }

    private void checkVersion() {
        LogTool.d(TAG+"checkVersion");
    }

    private void openAboutUs() {
        LogTool.d(TAG+"openAboutUs");
    }

    private void openUserAgreement() {
        LogTool.d(TAG+"openUserAgreement");
        Intent intent = new Intent(this,WebPageActivity.class);
        intent.putExtra("title","使用协议");
        intent.putExtra("web_addr", Utils.BASE_URL+"xieyi/");
        startActivity(intent);
    }

    private void callService() {
        LogTool.d(TAG + "callService");
        new AlertDialog.Builder(this)
                .setTitle("拨打热线")
                .setMessage("立即拨打客服热线021-61119485")
                .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dissmiss
                    }
                })
                .setPositiveButton(R.string.ok_msg,new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startCall();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void startCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:021-6119485"));
        startActivity(intent);
    }

    private void openFirmAccount() {
        LogTool.d(TAG+"openFirmAccount");
        if(LizoneApp.mCanLogin) {
            startActivity(new Intent(this,OpenFirmAccountActivity.class));
        } else {
            gotoLogin();
        }
    }

    private void openPersonalCenter() {
        LogTool.d(TAG+"openPersonalCenter");
        if(LizoneApp.mCanLogin) {
            startActivity(new Intent(this,PersonalActivity.class));
        } else {
            gotoLogin();
        }
    }
    private void gotoLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
