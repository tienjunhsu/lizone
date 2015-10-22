package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PC on 2015/9/8.
 */
public class AccountItem {
    public String id;
    public String name;
    public String userid;
    public String rank;
    public String integral;
    public String photo;
    public String profile;
    public String sub_num;
    public String fans_num;
    public String view_num;

    public AccountItem(String id,String name,String userid,String rank,String integral,String photo,String profile,String sub_num,String fans_num,String view_num) {
        this.id=id;
        this.name = name;
        this.userid = userid;
        this.rank = rank;
        this.integral = integral;
        this.photo = photo;
        this.profile = profile;
        this.sub_num = sub_num;
        this.fans_num = fans_num;
        this.view_num = view_num;
    }
    public static AccountItem getItem(JSONObject obj_s) {
        JSONObject obj;
        try {
            obj =obj_s.getJSONObject("data");
        } catch (JSONException e) {
            return null;
        }
        String id = JsnTool.getString(obj, "id");
        String name=JsnTool.getString(obj, "name");
        String userid = JsnTool.getString(obj, "userid");
        String rank = JsnTool.getString(obj, "rank");
        String integral = JsnTool.getString(obj, "integral");
        String photo = JsnTool.getString(obj, "photo");
        String profile = JsnTool.getString(obj, "profile");
        String sub_num= JsnTool.getString(obj, "dingy");
        String fans_num = JsnTool.getString(obj, "fens");
        String view_num = JsnTool.getString(obj, "jiaoy");
        return new AccountItem(id, name, userid, rank,integral,photo,profile,sub_num,fans_num,view_num);
    }
    public static AccountItem getItem(String str) {
        try {
            return getItem(JsnTool.getObject(str).getJSONObject("data"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
