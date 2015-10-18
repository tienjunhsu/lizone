package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepageRecordItem {

    public String id;
    public String user_id;
    public String style;
    public String time;
    public String text;
    public String ex_id;
    public String system;
    public String ex_name;
    public String shouyi;
    public String zan;
    public String pinglun;
    public String ex_code;

    public HomepageRecordItem(String id, String user_id, String style, String time, String text, String ex_id, String system, String ex_name, String shouyi, String zan,String pinglun, String ex_code) {
        this.id=id;
        this.user_id =user_id;
        this.style=style;
        this.time = time;
        this.text =text;
        this.ex_id = ex_id;
        this.system = system;
        this.ex_name = ex_name;
        this.shouyi = shouyi;
        this.zan = zan;
        this.pinglun = pinglun;
        this.ex_code = ex_code;

    }

    public static HomepageRecordItem getItem(JSONObject obj) {
        String id= JsnTool.getString(obj, "id");
        String user_id= JsnTool.getString(obj, "user_id");
        String style= JsnTool.getString(obj, "style");
        String time= JsnTool.getString(obj, "time");
        String text= JsnTool.getString(obj, "text");
        String ex_id= JsnTool.getString(obj, "ex_id");
        String system= JsnTool.getString(obj, "system");
        String ex_name = JsnTool.getString(obj, "ex_name");
        String shouyi= JsnTool.getString(obj, "shouyi");
        String zan= JsnTool.getString(obj, "zan");
        String pinglun= JsnTool.getString(obj, "pinglun");
        String ex_code= JsnTool.getString(obj, "ex_code");
        return new HomepageRecordItem(id, user_id, style, time, text, ex_id,system, ex_name, shouyi, zan,pinglun, ex_code);
    }

    public static ArrayList<HomepageRecordItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<HomepageRecordItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<HomepageRecordItem> getItemList(JSONArray ary) {
        ArrayList<HomepageRecordItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<HomepageRecordItem>(n);
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
            list = new ArrayList<HomepageRecordItem>(0);
        }
        return list;
    }
}