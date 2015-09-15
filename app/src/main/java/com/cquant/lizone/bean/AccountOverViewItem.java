package com.cquant.lizone.bean;

import com.cquant.lizone.tool.JsnTool;

import org.json.JSONObject;

/**
 * Created by PC on 2015/9/15.
 */
public class AccountOverViewItem {
    public String floating;
    public String use_cash;
    public String follow_cash;
    public String total_capital;
    public String available_capital;
    public String risk_rate;
    public String position_capital;

    public AccountOverViewItem(String floating,String use_cash,String follow_cash,String total_capital,String available_capital,String risk_rate,String position_capital) {
        this.floating = floating;
        this.use_cash = use_cash;
        this.follow_cash = follow_cash;
        this.total_capital = total_capital;
        this.available_capital = available_capital;
        this.risk_rate = risk_rate;
        this.position_capital = position_capital;
    }
    public static AccountOverViewItem getItem(JSONObject obj) {
        String floating=JsnTool.getString(obj, "fdyk");
        String use_cash= JsnTool.getString(obj, "zyzc");
        String follow_cash = JsnTool.getString(obj, "gdzc");
        String total_capital = JsnTool.getString(obj, "zzc");
        String available_capital = JsnTool.getString(obj, "kyzc");
        String risk_rate = JsnTool.getString(obj, "fxl");
        String position_capital = JsnTool.getString(obj, "ccsz");
        return new AccountOverViewItem(floating,use_cash,follow_cash,total_capital,available_capital,risk_rate,position_capital);
    }
    public static AccountOverViewItem getItem(String str) {
        return getItem(JsnTool.getObject(str));
    }
}
