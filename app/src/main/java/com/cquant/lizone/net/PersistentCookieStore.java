package com.cquant.lizone.net;

import com.cquant.lizone.tool.LogTool;
import com.cquant.lizone.util.GlobalVar;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by asus on 2016/1/21.
 */
public class PersistentCookieStore implements CookieStore {

    private final HashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;

    public PersistentCookieStore() {
        cookies = new HashMap<String, ConcurrentHashMap<String, HttpCookie>>();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        LogTool.e("add cookie,uri ="+uri+",cookie="+cookie);
        //if (!cookie.hasExpired()) {
        if(uri.toString().contains("Login")) {
            cookies.put(cookie.getDomain(), new ConcurrentHashMap<String, HttpCookie>());
            cookies.get(cookie.getDomain()).put(cookie.getName(), cookie);
            GlobalVar.SESSIONID = cookie.toString();
            LogTool.d("add cookie :SESSIONID"+GlobalVar.SESSIONID);
        }
       // }
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        LogTool.e("get cookie,uri ="+uri);
        ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
        if(!uri.toString().contains("Login")) {
            for (String key : cookies.keySet()) {
                if (uri.getHost().contains(key)) {
                    ret.addAll(cookies.get(key).values());
                }
            }
        }
        LogTool.e("get cookie,uri ="+uri+",ret="+ret);
        return ret;
    }

    @Override
    public List<HttpCookie> getCookies() {
        LogTool.e("get cookie,uri ");
        ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
        for (String key : cookies.keySet()) {
            ret.addAll(cookies.get(key).values());
        }
        return ret;
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> ret = new ArrayList<URI>();
        for (String key : cookies.keySet())
            try {
                ret.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        return ret;
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        return false;
    }

    @Override
    public boolean removeAll() {
        cookies.clear();
        return true;
    }
}
