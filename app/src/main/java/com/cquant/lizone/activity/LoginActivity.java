package com.cquant.lizone.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cquant.lizone.R;
import com.cquant.lizone.net.WebHelper;
import com.cquant.lizone.tool.JsnTool;
import com.cquant.lizone.util.Utils;

import org.json.JSONObject;

/**
 * Created by asus on 2015/9/8.
 */
public class LoginActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextInputLayout mEditNameLayout;
    private TextInputLayout mEditPassLayout;

    private EditText mEditName;
    private EditText mEditPass;

    private WebHelper mWebhelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
        mWebhelper = new WebHelper(this);
    }

    private void initLayout() {
        setContentView(R.layout.login_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditNameLayout = (TextInputLayout) findViewById(R.id.edit_name);
        mEditPassLayout = (TextInputLayout) findViewById(R.id.edit_password);

        initToolBar();

        initTextName();
        initTextPass();
    }

    private void initTextName() {
        mEditName = mEditNameLayout.getEditText();
        mEditName.addTextChangedListener(new  TextInputLayoutWatcher(R.id.edit_name));
    }

    private void initTextPass() {
        mEditPass = mEditPassLayout.getEditText();
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.login);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        super.initToolBar(toolbar);
    }

    public void onXmlBtClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_verify:
                break;
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

    private boolean checkUserName() {
        if( TextUtils.isEmpty(mEditName.getText().toString().trim())) {
            mEditNameLayout.setErrorEnabled(true);
            mEditNameLayout.setError(getResources().getString(R.string.user_name_error));
            return false;
        } else {
            mEditNameLayout.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    public class TextInputLayoutWatcher implements   TextWatcher {
        private final int  mLayoutId;

        public  TextInputLayoutWatcher(int id){
            mLayoutId = id;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if(mLayoutId == R.id.edit_name) {
                mEditNameLayout.setErrorEnabled(false);
            } else if(mLayoutId == R.id.edit_password) {

            } else if(mLayoutId == R.id.edit_pass_confirm) {

            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
