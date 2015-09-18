package com.cquant.lizone.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android_websockets.WebSocketClient;
import com.cquant.lizone.R;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by asus on 2015/8/30.
 */
public class MarketFragment extends BaseFragment implements WebSocketClient.Listener {

    private WebSocketClient mSocket = null;

    private String url = "http://1-yj.com:3000";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.market_fragment, container, false);
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();;
    }
    public void connect() {
           // List<BasicNameValuePair> extraHeaders = new ArrayList<BasicNameValuePair>();
           // extraHeaders.add(new BasicNameValuePair("user", "renzh"));
           // extraHeaders.add(new BasicNameValuePair("age", "24"));
        Log.d("TianjunXu","connect");
            try {
                if (mSocket == null) {
                    mSocket = new WebSocketClient(new URI(url), this,
                            null);
                }
                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.d("TianjunXu", "connect:URISyntaxException:"+e.getMessage());
            }

        //test
       /* WebHelper mWebhelper = new WebHelper(getActivity());
        mWebhelper.doLoadGet(url, null, new WebHelper.OnWebFinished() {

            @Override
            public void onWebFinished(boolean success, String msg) {
                Log.d("TianjunXu", " connect:success = " + success + ",msg =" + msg);
            }
        });*/
        //test
    }

    public void disconnected() {
        if (mSocket != null && mSocket.isConnected()) {
            mSocket.disconnect();
        }
    }
    private Handler mHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            } else if (msg.what == 2) {

            }else if(msg.what==3){
                connect();
            }
            super.handleMessage(msg);
        }

    };
    @Override
    public void onResume() {
        super.onResume();
        connect();
    }
    @Override
    public void onPause() {
        super.onPause();
        disconnected();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnect() {

        Log.d("TianjunXu","Socket:onConnect");

       // mHanler.sendEmptyMessage(1);
    }

    @Override
    public void onMessage(String message) {

        Log.d("TianjunXu", "Socket:onMessage:message="+message);

    }

    @Override
    public void onMessage(byte[] data) {
        /*Message msg = new Message();
        msg.what = 2;
        msg.obj = data;
        mHanler.sendMessage(msg);*/
        Log.d("TianjunXu","Socket:onMessage:data="+new String(data));




    }

    @Override
    public void onDisconnect(int code, String reason) {

        //if(isVisible){
            mHanler.sendEmptyMessageDelayed(3, 100);
        //}
        Log.d("TianjunXu","Socket:onDisconnect:code="+code+",reason="+reason);
    }

    @Override
    public void onError(Exception error) {
        Log.d("TianjunXu","Socket:onError:"+error.getMessage());

    }
}
