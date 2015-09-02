package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/9/2.
 */
public class PromotionItem {

    public String img_url;
    public int sum = 0;
    public String url;
    public String name;

    public static PromotionItem getItem(JSONObject obj) {
        String mImg = JsnTool.getString(obj, "img");
        int mSum=JsnTool.getInt(obj, "sum");
        String mUrl=JsnTool.getString(obj, "url");
        String mName=JsnTool.getString(obj, "name");
        return new PromotionItem(mImg, mSum, mUrl, mName);
    }
    public static ArrayList<PromotionItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<PromotionItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<PromotionItem> getItemList(JSONArray ary) {
        ArrayList<PromotionItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<PromotionItem>(n);
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
            list = new ArrayList<PromotionItem>(0);
        }
        return list;
    }
    public PromotionItem(String img_url,int sum,String url,String name) {
        this.img_url = img_url;
        this.sum = sum;
        this.url= url;
        this.name = name;
    }

}
