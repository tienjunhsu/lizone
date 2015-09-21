package com.cquant.lizone.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cquant.lizone.R;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by asus on 2015/8/30.
 */
public class MarketFragment extends BaseFragment {

    private Socket socket;

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

        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("TianjunXu","connect:URISyntaxException "+e.getMessage());
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("TianjunXu","connect:EVENT_CONNECT");
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("TianjunXu","connect:event");
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("TianjunXu","connect:EVENT_DISCONNECT");
            }

        }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("TianjunXu","connect:EVENT_MESSAGE");
            }
        }).on("notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject)args[0];
                Log.d("TianjunXu", "connect:notification-->data="+data);
            }
        });;
        socket.connect();

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
}
