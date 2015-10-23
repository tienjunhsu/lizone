package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.tool.AppTool;
import com.cquant.lizone.tool.LogTool;

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
        mTvVersion.setText("V"+ AppTool.getVersionName(LizoneApp.getApp()));
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
    }

    private void callService() {
        LogTool.d(TAG+"callService");
    }

    private void openFirmAccount() {
        LogTool.d(TAG+"openFirmAccount");
    }

    private void openPersonalCenter() {
        LogTool.d(TAG+"openPersonalCenter");
    }
}
