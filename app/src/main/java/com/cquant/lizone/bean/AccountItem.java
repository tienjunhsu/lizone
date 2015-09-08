package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONObject;

/**
 * Created by PC on 2015/9/8.
 */
public class AccountItem {
    public int id;
    public String name;
    public String userid;
    public int rank;
    public int integral;
    public String photo;
    public String profile;
    public int sub_num;
    public int fans_num;
    public int view_num;

    public AccountItem(int id,String name,String userid,int rank,int integral,String photo,String profile,int sub_num,int fans_num,int view_num) {
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
    public static AccountItem getItem(JSONObject obj) {
        int id = JsnTool.getInt(obj, "id");
        String name=JsnTool.getString(obj, "name");
        String userid = JsnTool.getString(obj, "userid");
        int rank = JsnTool.getInt(obj, "rank");
        int integral = JsnTool.getInt(obj, "integral");
        String photo = JsnTool.getString(obj, "photo");
        String profile = JsnTool.getString(obj, "profile");
        int sub_num= JsnTool.getInt(obj, "dingy");
        int fans_num = JsnTool.getInt(obj, "fens");
        int view_num = JsnTool.getInt(obj, "jiaoy");
        return new AccountItem(id, name, userid, rank,integral,photo,profile,sub_num,fans_num,view_num);
    }
    public static AccountItem getItem(String str) {
        return getItem(JsnTool.getObject(str));
    }
}
