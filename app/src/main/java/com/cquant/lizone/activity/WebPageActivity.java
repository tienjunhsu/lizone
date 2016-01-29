package com.cquant.lizone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cquant.lizone.R;
import com.cquant.lizone.net.LoginWatcher;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.Utils;

/**
 * Created by asus on 2015/10/21.
 */
public class WebPageActivity extends BaseActivity {
    //通用于展示web页
    private Toolbar toolbar;
    private WebView webview;

 //   private String web_addr;
    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_web_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webview = (WebView)findViewById(R.id.webview);

        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("web_addr");

        //url = Utils.BASE_URL + web_addr;

        initToolBar();

        initWebView();
        //setSession();
        webLogin();
        webview.loadUrl(url);
    }

    private void setSession() {
        if(GlobalVar.SESSIONID == null) {
            return;
        }
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, "PHPSESSID=" + GlobalVar.SESSIONID);
        CookieSyncManager.getInstance().sync();
        cookieManager.setAcceptCookie(true);

    }
    private void webLogin(){
        WebView mWebView = new WebView(this);
        mWebView.loadUrl(Utils.BASE_URL + "Login?"+ LoginWatcher.getLoginStr());
    }

    private void initWebView() {
        WebSettings wSettings = webview.getSettings();
        wSettings.setAllowContentAccess(true);
        wSettings.setAllowFileAccess(true);
        wSettings.setAllowFileAccessFromFileURLs(true);
        wSettings.setAllowUniversalAccessFromFileURLs(true);
        wSettings.setAppCacheEnabled(true);
        wSettings.setBlockNetworkImage(false);
        wSettings.setBlockNetworkLoads(false);
        wSettings.setDomStorageEnabled(true);
        wSettings.setJavaScriptEnabled(true);
        wSettings.setDefaultTextEncodingName("UTF -8") ;
        wSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        wSettings.setLoadsImagesAutomatically(true);
        webview.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //startLoadingAnim();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("Login")) {
                    startLogin();
                } else {
                    webview.loadUrl(url);
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // stopLoadingAnim();
            }
        });
    }

    private void startLogin() {
        startActivity(new Intent(this,LoginActivity.class));
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle(title);
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

    }
}
