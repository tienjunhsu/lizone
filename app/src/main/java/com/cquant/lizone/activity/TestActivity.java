package com.cquant.lizone.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cquant.lizone.R;

/**
 * Created by PC on 2015/8/24.
 */
public class TestActivity extends BaseActivity {

    private WebView webview;
    private  String url ="http://www.lizone.net/index.php/api/index/UserHome/uid/198";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        webview = (WebView)findViewById(R.id.webview);
        initWebView();
        webview.loadUrl(url);
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
                return false;
            }
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // stopLoadingAnim();
            }
        });
    }
}
