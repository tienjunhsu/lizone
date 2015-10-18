package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepageViewItem {
    public String ex_code;
    public String ex_name;
    public String body;
    public String time;

    public HomepageViewItem(String ex_name, String ex_code, String body, String time) {
        this.ex_name = ex_name;
        this.ex_code = ex_code;
        this.body = body;
        this.time = time;
    }

    public static HomepageViewItem getItem(JSONObject obj) {
        String ex_name= JsnTool.getString(obj, "ex_name");
        String ex_code = JsnTool.getString(obj, "ex_code");
        String body = JsnTool.getString(obj, "body");
        String time= JsnTool.getString(obj, "time");
        return new HomepageViewItem(ex_name, ex_code, body, time);
    }

    public static ArrayList<HomepageViewItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<HomepageViewItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<HomepageViewItem> getItemList(JSONArray ary) {
        ArrayList<HomepageViewItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<HomepageViewItem>(n);
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
            list = new ArrayList<HomepageViewItem>(0);
        }
        return list;
    }
}
