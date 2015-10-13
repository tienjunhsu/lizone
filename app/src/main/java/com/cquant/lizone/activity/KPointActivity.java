package com.cquant.lizone.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cquant.lizone.R;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.Utils;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by PC on 2015/10/9.
 */
public class KPointActivity extends BaseActivity {

    private Toolbar toolbar;
    private WebView webview;

    private String url = "http://www.1-yj.com/Kline/index.php/Index/Kline/label/XAGUSD/res/30/";
    //private String url = "http://www.1-yj.com/Kline/index.php/Index/Fenshi/label/XAGUSD/";
    //private String url = "http://finance.sina.com.cn/futures/quotes/AG1512.shtml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kpoint_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webview = (WebView)findViewById(R.id.webview);

        initToolBar();
        initWebView();
        webview.loadUrl(url);
    }

    private void initWebView() {
        WebSettings wSettings = webview.getSettings();
        wSettings.setJavaScriptEnabled(true);
        wSettings.setAppCacheEnabled(true);
        wSettings.setDomStorageEnabled(true);
        String appCacheDir = this.getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath();
        wSettings.setAppCachePath(appCacheDir);
        wSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        wSettings.setAppCacheMaxSize(1024 * 1024 * 10);
        wSettings.setAllowFileAccess(true);
        wSettings.setDatabaseEnabled(true);
        String databaseDir = this.getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        wSettings.setDatabasePath(databaseDir);
        wSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        wSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        wSettings.setUseWideViewPort(true);
        wSettings.setLoadWithOverviewMode(true);
        wSettings.setBuiltInZoomControls(true);
        wSettings.setSupportZoom(true);
        wSettings.enableSmoothTransition();
        wSettings.setEnableSmoothTransition(true);

        int screenDensity = getResources().getDisplayMetrics().densityDpi ;
        LogTool.d("screenDensity ="+screenDensity);
        WebSettings.ZoomDensity zoomDensity = WebSettings.ZoomDensity.MEDIUM ;
        if(screenDensity <=DisplayMetrics.DENSITY_MEDIUM) {
            zoomDensity = WebSettings.ZoomDensity.CLOSE;
        }else if(screenDensity <=DisplayMetrics.DENSITY_HIGH) {
            zoomDensity = WebSettings.ZoomDensity.MEDIUM;
        }else {
            zoomDensity = WebSettings.ZoomDensity.FAR;
        }
        wSettings.setDefaultZoom(zoomDensity);

        //wSettings.setSupportMultipleWindows();
       webview.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //startLoadingAnim();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*if (url.contains("login")) {
                    startLogin();
                }*/
                webview.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // stopLoadingAnim();
            }
        });
    }

    @Override
    protected void initToolBar() {
        toolbar.setTitle("现货白银");
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
