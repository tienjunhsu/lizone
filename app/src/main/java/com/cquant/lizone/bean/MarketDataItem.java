package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/21.
 */
public class MarketDataItem {
    public int id;
    public double  open;
    public String excode;
    public double sell;
    public double buy;
    public String name;
    public double newprice;
    public String label;
    public double high;
    public double low;
    public double close;

    public MarketDataItem(int id,double open,String excode,double sell,double buy,String name,double newprice,String label,double high,double low,double close) {
        this.id=id;
        this.open = open;
        this.excode = excode;
        this.sell = sell;
        this.buy = buy;
        this.name = name;
        this.newprice = newprice;
        this.label = label;
        this.high = high;
        this.low = low;
        this.close = close;

    }
    public static MarketDataItem getItem(JSONObject obj) {
        int id = JsnTool.getInt(obj, "id");
        double  open = JsnTool.getDouble(obj, "open");
        String excode = JsnTool.getString(obj,"excode");
        double sell = JsnTool.getDouble(obj, "sell");
        double buy = JsnTool.getDouble(obj, "buy");
        String name = JsnTool.getString(obj,"name");
        double newprice = JsnTool.getDouble(obj, "newprice");
        String label = JsnTool.getString(obj, "label");
        double high = JsnTool.getDouble(obj, "high");
        double low = JsnTool.getDouble(obj, "low");
        double close = JsnTool.getDouble(obj,"close");
        return new  MarketDataItem(id,open,excode,sell,buy,name,newprice,label,high,low,close);
    }
    public static ArrayList<MarketDataItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<MarketDataItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "articles"));
    }

    public static ArrayList<MarketDataItem> getItemList(JSONArray ary) {
        ArrayList<MarketDataItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<MarketDataItem>(n);
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
            list = new ArrayList<MarketDataItem>(0);
        }
        return list;
    }
}
