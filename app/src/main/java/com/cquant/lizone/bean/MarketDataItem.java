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
    public int  open;
    public String excode;
    public int sell;
    public int buy;
    public String name;
    public int newprice;
    public String label;
    public int high;
    public int low;
    public int close;

    public MarketDataItem(int id,int open,String excode,int sell,int buy,String name,int newprice,String label,int high,int low,int close) {
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
        int  open = JsnTool.getInt(obj,"open");
        String excode = JsnTool.getString(obj,"excode");
        int sell = JsnTool.getInt(obj,"sell");
        int buy = JsnTool.getInt(obj,"buy");
        String name = JsnTool.getString(obj,"name");
        int newprice = JsnTool.getInt(obj,"newprice");
        String label = JsnTool.getString(obj, "label");
        int high = JsnTool.getInt(obj,"high");
        int low = JsnTool.getInt(obj,"low");
        int close = JsnTool.getInt(obj,"close");
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
