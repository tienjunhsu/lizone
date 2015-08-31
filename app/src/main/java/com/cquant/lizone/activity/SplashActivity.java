package com.cquant.lizone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cquant.lizone.R;

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
        mAnimaInit.setDuration(800);
        mAnimaInit.setAnimationListener(this);
        mIvSplash.startAnimation(mAnimaInit);

    }
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
