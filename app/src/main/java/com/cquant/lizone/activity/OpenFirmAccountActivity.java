package com.cquant.lizone.activity;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cquant.lizone.R;
import com.cquant.lizone.net.LoginWatcher;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.Utils;

public class OpenFirmAccountActivity extends BaseActivity {

    private Toolbar toolbar;
    private WebView webview;

    private String url = Utils.BASE_URL + Utils.OPEN_FIRM_ADDR;
    //private String url = Utils.BASE_URL+"Build_Trade/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_firm_account_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webview = (WebView)findViewById(R.id.webview);

        initToolBar();

        initWebView();
        setSession();
        webview.loadUrl(url);
        webLogin();
    }
    private void webLogin(){
        WebView mWebView = new WebView(this);
        mWebView.loadUrl(Utils.BASE_URL+"Login?"+ LoginWatcher.getLoginStr());
    }
    private void setSession() {
        if(GlobalVar.SESSIONID == null) {
            return;
        }
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        cookieManager.setCookie(url, "PHPSESSID=" + GlobalVar.SESSIONID);
        LogTool.e("OpenFirmAccountActivity:setSession-->PHPSESSID=" + GlobalVar.SESSIONID);
        CookieSyncManager.getInstance().sync();
        cookieManager.setAcceptCookie(true);//hsu
        //cookieManager.setAcceptThirdPartyCookies(webview, true);

        //String cookie = cookieManager.getCookie(url);
        //LogTool.e("OpenFirmAccountActivity:setSession-->cookie=" + cookie);
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
                /*if (url.contains("login")) {
                    startLogin();
                }*/
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // stopLoadingAnim();
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);
                LogTool.e("OpenFirmAccountActivity:onPageFinished-->cookie=" + cookie + ",url=" + url);
            }
        });
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view,callback);
            }
            @Override
            public void onShowCustomView(View view, int requestedOrientation,
                                         CustomViewCallback callback) {
                super.onShowCustomView(view, requestedOrientation, callback);
            }
        });
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle(R.string.open_account);
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
            case R.id.btn_get_verify:
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.open_firm_account_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
