package com.cquant.lizone.util;

import com.cquant.lizone.bean.AccountItem;
import com.cquant.lizone.bean.MarketDataItem;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by PC on 2015/9/8.
 */
public class GlobalVar {

    public static String SESSIONID;

    public static AccountItem sAccountInf;

    public static Map<String,ArrayList<MarketDataItem>> sGroup;
}
