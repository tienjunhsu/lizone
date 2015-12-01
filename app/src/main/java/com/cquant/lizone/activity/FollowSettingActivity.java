package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cquant.lizone.R;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.tool.StrTool;
import com.cquant.lizone.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

/**
 * Created by asus on 2015/11/25.
 */
public class FollowSettingActivity extends BaseActivity {

    private static final String TAG = "FollowSettingActivity";
    private Toolbar toolbar;

    private WebHelper mWebhelper = null;
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
		mEditNum = (EditText)findViewById(R.id.number);
        mTvName = (TextView)findViewById(R.id.name);
        mIvPhoto = (ImageView)findViewById(R.id.photo);
        mTvNumTitle = (TextView)findViewById(R.id.tv_setting_title);
        mTvAvailable = (TextView)findViewById(R.id.tv_available);
		progress = (SeekBar)findViewById(R.id.progress);
        progress.setMax(100);
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mCaptical = i*mTotalCaptical/100;
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

        mWebhelper = new WebHelper(this);

    }

    private void setCapticalText() {
        mEditNum.setText(mCaptical+"");
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
       mTvName.setText(name);
       ImageLoader.getInstance().displayImage(photo, mIvPhoto);
       mTvAvailable.setText("可用资金："+mAvailableCaptical);

   }

    private void setFollow() {
        mWebhelper.doLoadGet(Utils.BASE_URL + "SetGend/gend_id/" + uid + "/money/" + mEditNum.getText().toString().trim(), null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                if(success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        mWebhelper.cancleRequest();
                        popMsg("跟单成功");
                        //处理跟单返回数据
                        setResult(RESULT_OK);//设置结果OK
                        finish();
                    } else {
                        popMsg("跟单失败");
                    }
                } else {
                   popMsg("跟单失败");
                }
            }
        });
    }
    private void getFollowData() {
        mWebhelper.doLoadGet(Utils.BASE_URL + "GendUserData/uid/" + uid, null, new WebHelper.OnWebFinished() {
            @Override
            public void onWebFinished(boolean success, String msg) {
                if(success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        mWebhelper.cancleRequest();
                        JSONObject obj = JsnTool.getObject(response,"data");
                        if(obj == null) {
                            popMsg("获取数据失败，请重试");
                            finish();
                            return;
                        }
                        photo = JsnTool.getString(obj, "photo");
                        name = JsnTool.getString(obj, "name");
                        mAvailableCaptical = JsnTool.getString(obj, "kyzj");
                        mTotalCaptical = StrTool.getDouble(mAvailableCaptical);
                        setViewData();
                    } else {
                        popMsg("获取数据失败，请重试");
                        finish();
                    }
                } else {
                    popMsg("获取数据失败，请重试");
                    finish();
                }
            }
        });
    }
}
