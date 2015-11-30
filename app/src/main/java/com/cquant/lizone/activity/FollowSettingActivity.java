package com.cquant.lizone.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cquant.lizone.R;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.util.Utils;

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
    private double mCaptical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = getIntent().getStringExtra("uid");

        setContentView(R.layout.edit_opt_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolBar();

        mWebhelper = new WebHelper(this);

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

    private void setFollow() {
        mWebhelper.doLoadGet(Utils.BASE_URL + "SetGend/gend_id/" + uid + "/money/" + mCaptical, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                if(success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        mWebhelper.cancleRequest();
                        popMsg("跟单成功");
                        //处理跟单返回数据
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
    private void cancleFollow() {
        mWebhelper.doLoadGet(Utils.BASE_URL + "Cancel_Gend/gend_id/" + uid, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                if(success) {
                    JSONObject response = JsnTool.getObject(msg);
                    if ((response != null) && (JsnTool.getInt(response, "status") == 1)) {
                        mWebhelper.cancleRequest();
                    } else {
                        popMsg("取消跟单失败");
                    }
                } else {
                    popMsg("取消跟单失败");
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
                        name = JsnTool.getString(obj, "kyzj");
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
