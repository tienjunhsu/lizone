package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/20.
 */
public class EventItem {
    //我的活动页面数据结构

    public String id;
    public String name;//活动标题
    public String  img;//活动图片
    public String body;
    //public String start_time;//unix时间戳，以秒计
    //public String end_time;
    //public String in_time;
    public String sum;
    public String status;
    //public String author;
    //public String min_img;
    public String desc;//描述
    public String num;//已经参加人数
    public String time;//显示时间
    public String url;

    public EventItem(String id,String name,String img,String body,String sum,String status,String desc,String num,String time,String url) {
        this.id=id;
        this.name = name;
        this.img = img;
        this.body = body;
        this.sum = sum;
        this.status = status;
        this.desc = desc;
        this.num = num;
        this.time = time;
        this.url = url;
    }
    public static EventItem getItem(JSONObject obj) {
        String id = JsnTool.getString(obj, "id");
        String name = JsnTool.getString(obj, "name");
        String img = JsnTool.getString(obj, "img");
        String body = JsnTool.getString(obj,"body");
        String sum = JsnTool.getString(obj, "sum");
        String status = JsnTool.getString(obj, "status");
        String desc = JsnTool.getString(obj, "desc");
        String num = JsnTool.getString(obj, "Renshu");
        String time = JsnTool.getString(obj,"time");
        String url = JsnTool.getString(obj, "url");
        return new EventItem(id,name,img,body,sum,status,desc,num,time,url);
    }
    public static ArrayList<EventItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<EventItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<EventItem> getItemList(JSONArray ary) {
        ArrayList<EventItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<EventItem>(n);
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
            list = new ArrayList<EventItem>(0);
        }
        return list;
    }
}
