package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/30.
 */
public class ViewsItem {
    //观点
    //交易记录用同一个

    public String userid;//用户id
    public String viewid;//观点id
    public String name;//用户名
    public String photo;//头像
    public String text;
    public String time;
    public String agree_num;//赞成数
    public String discuss_num;//评论数
    public String focus;//是否关注,关注是“y”,没有关注是"n"?

    public ViewsItem(String userid, String viewid, String name, String photo,String text,String time,String agree_num,String discuss_num,String focus) {
        this.userid = userid;
        this.viewid = viewid;
        this.name = name;
        this.photo = photo;
        this.text = text;
        this.time = time;
        this.agree_num = agree_num;
        this.discuss_num = discuss_num;
        this.focus = focus;
    }

    public static ViewsItem getItem(JSONObject obj) {
        String userid = JsnTool.getString(obj, "userid");
        String viewid = JsnTool.getString(obj, "guandid");
        String name = JsnTool.getString(obj, "name");
        String photo = JsnTool.getString(obj, "photo");
        String text = JsnTool.getString(obj, "text");
        String time = JsnTool.getString(obj, "time");
        String agree_num = JsnTool.getString(obj, "zan");
        String discuss_num = JsnTool.getString(obj, "pinglun");
        String focus = JsnTool.getString(obj, "guanzhu");
        return new ViewsItem(userid, viewid, name, photo,text,time,agree_num,discuss_num,focus);
    }

    public static ArrayList<ViewsItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<ViewsItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<ViewsItem> getItemList(JSONArray ary) {
        ArrayList<ViewsItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<ViewsItem>(n);
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
            list = new ArrayList<ViewsItem>(0);
        }
        return list;
    }
}
