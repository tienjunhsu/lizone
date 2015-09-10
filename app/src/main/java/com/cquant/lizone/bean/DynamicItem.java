package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/7.
 */
public class DynamicItem {

    //public int user_id;
    public String user_id;
    public int system;
    public String name;
    public String photo_url;
    public String time;
    public String text;
    //public int style;
    public String style;
    public String ex_name;
   // public int ex_id;
   public String ex_id;

   // public DynamicItem(int mUser_id, int mSystem, String mName, String mPhoto_url, String mTime, String mText, int mStyle, String mEx_name, int mEx_id) {
   public DynamicItem( String mUser_id, int mSystem, String mName, String mPhoto_url, String mTime, String mText,  String mStyle, String mEx_name,  String mEx_id) {
        this.user_id = mUser_id;
        this.system = mSystem;
        this.name = mName;
        this.photo_url = mPhoto_url;
        this.time = mTime;
        this.text = mText;
        this.style = mStyle;
        this.ex_name = mEx_name;
        this.ex_id = mEx_id;
    }

    public static DynamicItem getItem(JSONObject obj) {
        String mUser_id = JsnTool.getString(obj,"user_id");
        int mSystem = JsnTool.getInt(obj, "system");
        String mName=JsnTool.getString(obj, "name");
        String mPhoto_url = JsnTool.getString(obj, "photo_url");
        String mTime = JsnTool.getString(obj, "time");
        String mText = JsnTool.getString(obj, "text");
        String mStyle = JsnTool.getString(obj, "style");
        String mEx_name= JsnTool.getString(obj, "ex_name");
        String mEx_id = JsnTool.getString(obj, "ex_id");
        return new DynamicItem(mUser_id, mSystem, mName, mPhoto_url,mTime,mText,mStyle,mEx_name,mEx_id);
    }

    public static ArrayList<DynamicItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<DynamicItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<DynamicItem> getItemList(JSONArray ary) {
        ArrayList<DynamicItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<DynamicItem>(n);
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
            list = new ArrayList<DynamicItem>(0);
        }
        return list;
    }
}
