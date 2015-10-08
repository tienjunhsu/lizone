package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/10/8.
 */
public class OptItem {

    public String id;
    public String name;
    public String section;
    public String row;
    public String quoteid;
    public String label;

    private OptItem(String id,String name,String section,String row,String quoteid,String label) {
        this.id=id;
        this.name=name;
        this.section = section;
        this.row = row;
        this.quoteid = quoteid;
        this.label = label;
    }
    public static OptItem getItem(JSONObject obj) {
        String id = JsnTool.getString(obj, "id");
        String name = JsnTool.getString(obj, "name");
        String section = JsnTool.getString(obj, "section");
        String row = JsnTool.getString(obj, "row");
        String quoteid = JsnTool.getString(obj, "quoteid");
        String label = JsnTool.getString(obj, "label");

        return new OptItem(id,name,section,row,quoteid,label);
    }

    public static ArrayList<OptItem> getItemList(String json) {
        return getItemList(JsnTool.getObject(json));
    }

    public static ArrayList<OptItem> getItemList(JSONObject obj) {
        return getItemList(JsnTool.getArray(obj, "data"));
    }

    public static ArrayList<OptItem> getItemList(JSONArray ary) {
        ArrayList<OptItem> list = null;
        if (ary != null) {
            int n = ary.length();
            list = new ArrayList<OptItem>(n);
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
            list = new ArrayList<OptItem>(0);
        }
        return list;
    }
}
