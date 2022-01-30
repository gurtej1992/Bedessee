package com.bedessee.sales.utilities;

import android.util.Log;

public class ProtectedLog {

    final public static boolean SHOW_LOGS = false;


    public static void v(String tag, String message) {
        if(SHOW_LOGS) {
            Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if(SHOW_LOGS) {
            Log.w(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if(SHOW_LOGS) {
            Log.i(tag, message);
        }
    }

  public static void d(String tag, String message) {
        if(SHOW_LOGS) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message, Exception e) {
        if(SHOW_LOGS) {
            Log.e(tag, message, e);
        }
    }
}
