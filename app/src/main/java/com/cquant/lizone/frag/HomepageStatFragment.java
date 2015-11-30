package com.cquant.lizone.frag;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cquant.lizone.R;
import com.cquant.lizone.activity.HomepageActivity;
import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.Utils;
import com.cquant.lizone.view.MyWebView;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepageStatFragment extends BaseFragment {
    private static final String TAG = "HomepageStatFragment";

    //个人主页统计
    private String url;
    private WebView webview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.homepage_stat_frag, container, false);
        webview = (WebView)root.findViewById(R.id.webview);
        url = Utils.BASE_URL +"UserHome/uid/"+((HomepageActivity)getActivity()).getUserId();
        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        initWebView();
        webview.loadUrl(url);
        LogTool.v(TAG + "onResume");
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume,当前可见
            LogTool.v(TAG + "setUserVisibleHint to true");
            if(webview != null) {
                webview.loadUrl(url);
            }
        }
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
