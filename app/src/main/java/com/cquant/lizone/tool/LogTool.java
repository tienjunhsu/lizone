package com.cquant.lizone.tool;

import android.util.Log;

/**
 * Created by PC on 2015/10/8.
 */
public class LogTool {

    private static boolean isLoggable = true;
    private static String TAG = "TianjunXu";

    public static void d(String msg) {
        if(isLoggable) {
            Log.d(TAG, msg);
        }
    }
    public static void e(String msg) {
        if(isLoggable) {
            Log.e(TAG, msg);
        }
    }
    public static void v(String msg) {
        if(isLoggable) {
            Log.v(TAG, msg);
        }
    }
}
