package com.cquant.lizone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cquant.lizone.R;
import com.cquant.lizone.service.UpdateConfigService;
import com.cquant.lizone.tool.LogTool;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by PC on 2015/8/24.
 */
public class SplashActivity extends BaseActivity implements Animation.AnimationListener {

    private ImageView mIvSplash;
    private Animation mAnimaInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        mIvSplash = (ImageView) findViewById(R.id.iv_splash);
        mAnimaInit = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mAnimaInit.setDuration(3000);
        mAnimaInit.setAnimationListener(this);
        mIvSplash.startAnimation(mAnimaInit);
        if(!QbSdk.isTbsCoreInited()){
            QbSdk.preInit(this, myCallback);
        }
        startService(new Intent(this,UpdateConfigService.class));
    }
    private QbSdk.PreInitCallback myCallback=new QbSdk.PreInitCallback() {

        @Override
        public void onViewInitFinished() {
            // TODO Auto-generated method stub
            LogTool.d("PreInitCallback:onViewInitFinished");
        }

        @Override
        public void onCoreInitFinished() {
            // TODO Auto-generated method stub
            LogTool.d("PreInitCallback:onCoreInitFinished");
        }

    };
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        redictedTo();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void redictedTo() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    protected void initToolBar() {

    }
}
