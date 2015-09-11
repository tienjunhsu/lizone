package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/11.
 */
public class PositionItem {
    public String id;//记录id
    public String ex_name;//持仓品种名字
    public String ex_dir;//持仓方向(买入或者卖出)
    public String ex_number;//持仓手数
    public String start_price;//建仓价
    public String start_time;//建仓时间
    public String profit_number;//浮动盈亏
    public String use_capital;//占用保证金
    public String newprice;//最新价
    public String yield;//收益率

    public PositionItem( String id, String mName, String ex_dir, String ex_number, String start_price,  String start_time, String profit_number,  String use_capital, String newprice,  String yield) {
        this.id = id;
        this.ex_name= mName;
        this.ex_dir = ex_dir;
        this.ex_number = ex_number;
        this.start_price = start_price;
        this.start_time = start_time;
        this.profit_number = profit_number;
        this.use_capital = use_capital;
        this.newprice = newprice;
        this.yield = yield;
    }

    public static PositionItem getItem(JSONObject obj) {
        String id = JsnTool.getString(obj, "id");
        String ex_name = JsnTool.getString(obj, "ex_name");
        String ex_dir = JsnTool.getString(obj, "ex_dir");
        String ex_number = JsnTool.getString(obj, "ex_number");
        String start_price = JsnTool.getString(obj, "start_price");
        String start_time = JsnTool.getString(obj, "start_time");
        String profit_number = JsnTool.getString(obj, "profit_number");
        String use_capital = JsnTool.getString(obj, "use_capital");
        String newprice = JsnTool.getString(obj, "newprice");
        String yield = JsnTool.getString(obj, "yield");
        return new PositionItem(id, ex_name, ex_dir, ex_number, start_price,start_time, profit_number, use_capital,newprice, yield);
    }

    public static ArrayList<PositionItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<PositionItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<PositionItem> getItemList(JSONArray ary) {
        ArrayList<PositionItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<PositionItem>(n);
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
            list = new ArrayList<PositionItem>(0);
        }
        return list;
    }
}
