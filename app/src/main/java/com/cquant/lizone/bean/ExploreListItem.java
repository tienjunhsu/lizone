package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by asus on 2015/9/13.
 */
public class ExploreListItem {
    public String id;
    public String  photo;
    public String name;
    public String yield;
    public String num;//cishu
    public String success;

    public ExploreListItem(String id,String photo,String name,String yield,String num,String success) {
        this.id=id;
        this.photo = photo;
        this.name = name;
        this.yield = yield;
        this.num = num;
        this.success = success;
    }
    public static ExploreListItem getItem(JSONObject obj) {
        String id = JsnTool.getString(obj, "id");
        String photo = JsnTool.getString(obj,"photo");
        String name = JsnTool.getString(obj, "name");
        String yield = JsnTool.getString(obj,"yield");
        String num = JsnTool.getString(obj, "cishu");
        String success = JsnTool.getString(obj,"success");
        return new ExploreListItem(id,photo,name,yield,num,success);
    }
    public static ArrayList<ExploreListItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<ExploreListItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<ExploreListItem> getItemList(JSONArray ary) {
        ArrayList<ExploreListItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<ExploreListItem>(n);
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
            list = new ArrayList<ExploreListItem>(0);
        }
        return list;
    }
}
