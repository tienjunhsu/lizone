package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class NewsItem {

    public String id;
    public String title;
    public String time;
    public String info;

    public NewsItem(String id, String title, String time, String info) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.info = info;
    }

    public static NewsItem getItem(JSONObject obj) {
        String mId = JsnTool.getString(obj, "id");
        String mTitle = JsnTool.getString(obj, "title");
        String mTime = JsnTool.getString(obj, "time");
        String mInfo = JsnTool.getString(obj, "info");
        return new NewsItem(mId, mTitle, mTime, mInfo);
    }

    public static ArrayList<NewsItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<NewsItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<NewsItem> getItemList(JSONArray ary) {
        ArrayList<NewsItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<NewsItem>(n);
            JSONObject obj = null;
            for (int i = 0; i < n; i++) {
                try {
                    obj = ary.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (obj != null) {
                    list.add(getItem(obj));
                }
            }
        } else {
            list = new ArrayList<NewsItem>(0);
        }
        return list;
    }
}