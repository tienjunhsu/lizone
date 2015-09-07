package com.cquant.lizone.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by asus on 2015/9/5.
 */
public class SharedPrefsUtil {

    public static final String PREFS_PROMOTIONS = "promotions_list";
    public static final String PREFS_USER_ID = "user_id";
    public static final String PREFS_PASS = "pass_word";

    /** 数据存储的XML名称 **/
    public final static String SETTING = "cquant";

    /**
     * 存储数据(Long)
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putLongValue(Context context, String key, long value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putLong(key, value);
        sp.commit();
    }

    /**
     * 存储数据(Int)
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putIntValue(Context context, String key, int value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putInt(key, value);
        sp.commit();
    }

    /**
     * 存储数据(String)
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putStringValue(Context context, String key, String value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putString(key, value);
        sp.commit();
    }

    /**
     * 存储数据(boolean)
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBooleanValue(Context context, String key,
                                       boolean value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putBoolean(key, value);
        sp.commit();
    }

    /**
     * 取出数据（Long）
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static long getLongValue(Context context, String key, long defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        long value = sp.getLong(key, defValue);
        return value;
    }

    /**
     * 取出数据（int）
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getIntValue(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        int value = sp.getInt(key, defValue);
        return value;
    }

    /**
     * 取出数据（boolean）
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBooleanValue(Context context, String key,
                                          boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        boolean value = sp.getBoolean(key, defValue);
        return value;
    }

    /**
     * 取出数据（String）
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getStringValue(Context context, String key,
                                        String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return value;
    }

    /**
     * 清空所有数据
     *
     * @param context
     * @return
     */
    public static void clear(Context context) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.clear();
        sp.commit();
    }

    /**
     * 清空所有数据
     *
     * @param context
     * @param key
     * @return
     */
    public static void remove(Context context, String key) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.remove(key);
        sp.commit();
    }
}
