package com.cquant.lizone.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by asus on 2015/11/25.
 */
public class FollowSettingActivity extends BaseActivity {

    private static final String TAG = "FollowSettingActivity";
    private Toolbar toolbar;

    private String uid;

    private String photo;//用户头像名
    private String name;//用户名
    private String mAvailableCaptical;//可用资金
    private double mTotalCaptical;
    private double mCaptical = 0;

    private ImageView mIvPhoto;
    private TextView mTvName;
    private TextView mTvNumTitle;
    private TextView mTvAvailable;

    private EditText mEditNum;
    private Button mSubmit;

    private SeekBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = getIntent().getStringExtra("uid");

        setContentView(R.layout.follow_setting_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSubmit = (Button) findViewById(R.id.submit);
        mEditNum = (EditText) findViewById(R.id.number);
        mTvName = (TextView) findViewById(R.id.name);
        mIvPhoto = (ImageView) findViewById(R.id.photo);
        mTvNumTitle = (TextView) findViewById(R.id.tv_setting_title);
        mTvAvailable = (TextView) findViewById(R.id.tv_available);
        progress = (SeekBar) findViewById(R.id.progress);
        progress.setMax(100);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCaptical = i * mTotalCaptical / 100;
                setCapticalText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        initToolBar();

        if (isNetAvailable()) {
            getFollowData();
        } else {
            popMsg("网络不可用，请检查您的网络设置");
            finish();
        }

    }

    private void setCapticalText() {
        mEditNum.setText(mCaptical + "");
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("跟单设置");
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
            case R.id.submit:
                setFollow();
                break;
            default:
                break;
        }
    }

    private void setViewData() {
        //mTvName.setText(name);
        //ImageLoader.getInstance().displayImage(photo, mIvPhoto);//hsu,2015/12/21，暂时把跟单用户信息去掉，这个好像没有任何意义
        mTvAvailable.setText("可用资金：" + mAvailableCaptical);

    }

    private void setFollow() {
        if (!isNetAvailable()) {
            popMsg("网络不可用，请检查您的网络设置");
            return;
        }
        Request request = new Request.Builder().url(Utils.BASE_URL + "SetGend/gend_id/" + uid + "/money/" + mEditNum.getText().toString().trim()).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage();
                message.what = MSG_POP_MSG;
                message.obj = "跟单失败";
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    ;
                    JSONObject obj = JsnTool.getObject(msg);
                    if ((obj != null) && (JsnTool.getInt(obj, "status") == 1)) {
                        Message message = mHandler.obtainMessage();
                        message.what = MSG__FOLLOW_SUCCESSED;
                        message.sendToTarget();
                    } else {
                        String tips = JsnTool.getString(obj, "msg");
                        if (StrTool.areEmpty(tips)) {
                            tips = "跟单失败";
                        }
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_POP_MSG;
                        message.obj = tips;
                        message.sendToTarget();
                    }
                } else {
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_POP_MSG;
                    message.obj = "跟单失败";
                    message.sendToTarget();
                }
            }
        });
    }

    private void getFollowData() {
        Request request = new Request.Builder().url(Utils.BASE_URL + "GendUserData/uid/" + uid).tag(TAG).build();
        LizoneApp.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                Message message = mHandler.obtainMessage();
                message.what = MSG_GET_DATA_FAILED;
                message.sendToTarget();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    JSONObject obj = JsnTool.getObject(msg);
                    try {
                        if ((obj != null) && (obj.getInt("status") == 1)) {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_PARSE_FOLLOW_DATA;
                            message.obj = obj;
                            message.sendToTarget();
                        } else {
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_GET_DATA_FAILED;
                            message.sendToTarget();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message message = mHandler.obtainMessage();
                        message.what = MSG_GET_DATA_FAILED;
                        message.sendToTarget();
                    }
                } else {
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_GET_DATA_FAILED;
                    message.sendToTarget();
                }
            }
        });
    }

    private void parseFolowData(JSONObject response) {
        JSONObject obj = JsnTool.getObject(response, "data");
        if (obj == null) {
            popMsg("获取数据失败，请重试");
            finish();
            return;
        }
        photo = JsnTool.getString(obj, "photo");
        name = JsnTool.getString(obj, "name");
        mAvailableCaptical = JsnTool.getString(obj, "kyzj");
        mTotalCaptical = StrTool.getDouble(mAvailableCaptical);
        setViewData();
    }

    private void followSuccessed() {
        popMsg("跟单成功");
        //处理跟单返回数据
        setResult(RESULT_OK);//设置结果OK
        finish();
    }

    private static final int MSG_GET_DATA_FAILED = 11;
    private static final int MSG_PARSE_FOLLOW_DATA = 12;
    private static final int MSG_POP_MSG = 13;
    private static final int MSG__FOLLOW_SUCCESSED = 14;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_DATA_FAILED:
                    popMsg("获取数据失败，请重试");
                    finish();
                    break;
                case MSG_PARSE_FOLLOW_DATA:
                    parseFolowData((JSONObject) msg.obj);
                    break;
                case MSG_POP_MSG:
                    popMsg((String) msg.obj);
                    break;
                case MSG__FOLLOW_SUCCESSED:
                    followSuccessed();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onDestroy() {
        LizoneApp.getOkHttpClient().cancel(TAG);
        super.onDestroy();
    }
}
