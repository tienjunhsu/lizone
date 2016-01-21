package com.cquant.lizone.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cquant.lizone.LizoneApp;
import com.cquant.lizone.R;
import com.cquant.lizone.net.LoginWatcher;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.GlobalVar;
import com.cquant.lizone.util.Utils;

import java.util.Map;

/**
 * Created by PC on 2015/9/9.
 */
public class OpenPositionActivity extends BaseActivity  {
    private Toolbar toolbar;
    private WebView webview;

    //private String url = Utils.BASE_URL + Utils.OPEN_FIRM_ADDR;
    private String url = Utils.BASE_URL+"Build_Trade/";

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
        webLogin();//hsu
        //displayDataBase();
    }
    private void webLogin(){
        WebView mWebView = new WebView(this);
        mWebView.loadUrl(Utils.BASE_URL+"Login?"+ LoginWatcher.getLoginStr());
    }

    private void displayDataBase() {
        Log.e("HSU","displayDataBase");
        Context context = LizoneApp.getApp();
        String[] datalist = context.databaseList();
        for(String item:datalist) {
            Log.e("HSU","database:"+item);
        }
    }
    private void setSession() {
        if(GlobalVar.SESSIONID == null) {
            return;
        }
       //if(CookieSyncManager.getInstance() == null) {
           CookieSyncManager.createInstance(this);
      // } else {

      // }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        //cookieManager.removeAllCookie();
        cookieManager.setCookie(url, "PHPSESSID=" + GlobalVar.SESSIONID);
        //LogTool.e("OpenPositionActivity:setSession-->PHPSESSID="+GlobalVar.SESSIONID);
        //String cookie = cookieManager.getCookie(url);
        //LogTool.e("OpenPositionActivity:setSession-->cookie="+cookie);
        CookieSyncManager.getInstance().sync();
        //cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webview, true);

    }
    private void initWebView() {
        WebSettings wSettings = webview.getSettings();
        wSettings.setAllowContentAccess(true);
        wSettings.setAllowFileAccess(true);
        wSettings.setAllowFileAccessFromFileURLs(true);
        wSettings.setAllowUniversalAccessFromFileURLs(true);
        wSettings.setDatabaseEnabled(true);
        wSettings.setAppCacheEnabled(true);
        //wSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wSettings.setBlockNetworkImage(false);
        wSettings.setBlockNetworkLoads(false);
        wSettings.setDomStorageEnabled(true);
        wSettings.setJavaScriptEnabled(true);
        wSettings.setDefaultTextEncodingName("UTF -8");
        wSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        wSettings.setLoadsImagesAutomatically(true);
        //setSession();

        webview.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //startLoadingAnim();
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);
                LogTool.e("OpenPositionActivity:onPageStarted-->cookie="+cookie);
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.contains("login")) {
                    startLogin();
                }*/
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);
                LogTool.e("OpenPositionActivity:shouldOverrideUrlLoading-->cookie="+cookie+",url="+url);
                return false;
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // stopLoadingAnim();
                CookieManager cookieManager = CookieManager.getInstance();
                String cookie = cookieManager.getCookie(url);
                LogTool.e("OpenPositionActivity:onPageFinished-->cookie="+cookie);
            }

           /* public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
               String murl =  request.getUrl().toString();
                String method = request.getMethod();
                Map<String,String> headers = request.getRequestHeaders();
                headers.put("Cookie","PHPSESSID=" + GlobalVar.SESSIONID);
                String head = "";
                for (String s : headers.keySet()) {
                    head = head+";"+s;
                    head=head+":"+headers.get(s);
                }
                LogTool.e("OpenPositionActivity:shouldInterceptRequest->head="+head);
                LogTool.e("OpenPositionActivity:shouldInterceptRequest-->murl="+murl+",method="+method);
                return null;
            }*/
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
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
        toolbar.setTitle("建仓");
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
