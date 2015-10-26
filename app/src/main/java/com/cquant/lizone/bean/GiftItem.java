package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/10/25.
 */
public class GiftItem {
    //K线页财经热点、市场要闻

    public String id;
    public String name;
    public String price;
    public String img;
    public String big_img;
    public String rmb;
    public String number;

    public GiftItem(String id, String name, String price, String img,String big_img,String rmb,String number) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.big_img = big_img;
        this.rmb = rmb;
        this.number = number;
    }

    public static GiftItem getItem(JSONObject obj) {
        String mId = JsnTool.getString(obj, "id");
        String mName = JsnTool.getString(obj, "name");
        String mPrice = JsnTool.getString(obj, "price");
        String mImg = JsnTool.getString(obj, "min_img");
        String big_img = JsnTool.getString(obj, "img");
        String rmb = JsnTool.getString(obj, "rmb");
        String number = JsnTool.getString(obj, "number");
        return new GiftItem(mId, mName, mPrice, mImg,big_img,rmb,number);
    }

    public static ArrayList<GiftItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<GiftItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<GiftItem> getItemList(JSONArray ary) {
        ArrayList<GiftItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<GiftItem>(n);
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
            list = new ArrayList<GiftItem>(0);
        }
        return list;
    }
}
