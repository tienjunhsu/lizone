package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepagePositionItem {

    public String ex_name;//持仓品种名字
    public String ex_code;
    public String start_price;//建仓价
    public String newprice;//最新价
    public String ex_number;//持仓手数
    public String capital;//持仓市值
    public String profit_number;//浮动盈亏
    public String yield;//收益率

    public HomepagePositionItem(  String mName, String ex_code, String ex_number, String start_price, String newprice, String capital,String profit_number,    String yield) {
        this.ex_name= mName;
        this.ex_code = ex_code;
        this.ex_number = ex_number;
        this.start_price = start_price;
        this.profit_number = profit_number;
        this.capital = capital;
        this.newprice = newprice;
        this.yield = yield;
    }

    public static HomepagePositionItem getItem(JSONObject obj) {
        String ex_name = JsnTool.getString(obj, "ex_name");
        String ex_code = JsnTool.getString(obj, "ex_code");
        String ex_number = JsnTool.getString(obj, "ex_number");
        String start_price = JsnTool.getString(obj, "start_price");
        String newprice = JsnTool.getString(obj, "newprice");
        String capital = JsnTool.getString(obj, "capital");
        String profit_number = JsnTool.getString(obj, "profit_number");
        String yield = JsnTool.getString(obj, "yield");
        return new HomepagePositionItem(ex_name, ex_code, ex_number, start_price,newprice, capital,profit_number,yield);
    }

    public static ArrayList<HomepagePositionItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<HomepagePositionItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<HomepagePositionItem> getItemList(JSONArray ary) {
        ArrayList<HomepagePositionItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<HomepagePositionItem>(n);
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
            list = new ArrayList<HomepagePositionItem>(0);
        }
        return list;
    }
}
