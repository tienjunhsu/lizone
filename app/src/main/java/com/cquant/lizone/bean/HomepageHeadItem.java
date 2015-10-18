package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asus on 2015/10/18.
 */
public class HomepageHeadItem {
    public String id;//USER ID
    public String  photo;//头像地址
    public String name;//用户名
    public String profile;//简介
    public String yield;//年化收益率
    public String fans;//粉丝数
    public String sub_num;//订阅数
    public String trade_num;//交易次数
    public int gend;//跟单与否
    public int dingys;//是否订阅了

    public HomepageHeadItem(String id,String photo,String name,String profile,String yield,String fans,String sub_num,String trade_num,int gend,int dingys) {
        this.id=id;
        this.photo = photo;
        this.name = name;
        this.profile = profile;
        this.yield = yield;
        this.fans = fans;
        this.sub_num = sub_num;
        this.trade_num = trade_num;
        this.gend =gend;
        this.dingys = dingys;
    }
    public static HomepageHeadItem getItem(JSONObject obj) {
        String id = JsnTool.getString(obj, "id");
        String  photo= JsnTool.getString(obj, "photo");
        String name= JsnTool.getString(obj, "name");
        String profile= JsnTool.getString(obj, "profile");
        String yield= JsnTool.getString(obj, "yield");
        String fans= JsnTool.getString(obj, "fans");
        String sub_num= JsnTool.getString(obj, "dingy");
        String trade_num= JsnTool.getString(obj, "jiaoy");
        int gend= JsnTool.getInt(obj, "Gend");
        int dingys=JsnTool.getInt(obj, "DingYs");
        return new HomepageHeadItem(id,photo,name,profile,yield,fans,sub_num,trade_num,gend,dingys);
    }
    public static HomepageHeadItem getItem(String str) {
        try {
            return getItem(JsnTool.getObject(str).getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
