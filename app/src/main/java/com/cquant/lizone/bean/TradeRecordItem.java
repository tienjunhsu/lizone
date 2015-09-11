package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/11.
 */
public class TradeRecordItem {

    public String ex_name;//品种名称
    public String ex_code;//品种代码
    public String start_label;//建仓位置和方向
    public String ex_number;//手数
    public String start_time;//建仓时间
    public String end_label;//平仓位置和方向
    public String end_number;//平仓手数
    public String end_time;//平仓时间
    public String profit;//盈亏
    public String yield;//收益率

    public TradeRecordItem( String ex_name, String ex_code, String start_label,String ex_number, String start_time, String end_label,  String end_number, String end_time,String profit,  String yield) {
        this.ex_name = ex_name;
        this.ex_code= ex_code;
        this.start_label = start_label;
        this.ex_number = ex_number;
        this.start_time = start_time;
        this.end_label = end_label;
        this.end_number = end_number;
        this.end_time = end_time;
        this.profit = profit;
        this.yield = yield;
    }

    public static TradeRecordItem getItem(JSONObject obj) {
        String ex_name = JsnTool.getString(obj, "ex_name");
        String ex_code = JsnTool.getString(obj, "ex_code");
        String start_label = JsnTool.getString(obj, "start_label");
        String ex_number = JsnTool.getString(obj, "ex_number");
        String start_time = JsnTool.getString(obj, "start_time");
        String end_label= JsnTool.getString(obj, "end_label");
        String end_number = JsnTool.getString(obj, "end_number");
        String end_time = JsnTool.getString(obj, "end_time");
        String profit = JsnTool.getString(obj, "profit");
        String yield = JsnTool.getString(obj, "yield");
        return new TradeRecordItem(ex_name, ex_code, start_label, ex_number, start_time, end_label,  end_number, end_time,profit,yield);
    }

    public static ArrayList<TradeRecordItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<TradeRecordItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<TradeRecordItem> getItemList(JSONArray ary) {
        ArrayList<TradeRecordItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<TradeRecordItem>(n);
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
            list = new ArrayList<TradeRecordItem>(0);
        }
        return list;
    }
}
