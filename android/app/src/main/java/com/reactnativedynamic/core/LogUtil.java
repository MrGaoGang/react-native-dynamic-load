package com.reactnativedynamic.core;

import android.util.Log;

/**
 * Author: alexganggao
 * Created by: 2021/8/5
 * Description:
 */
public class LogUtil {
    private static String buildWholeMessage(String format, Object... args) {
        if(args != null && args.length != 0) {
            return "[now]"+ String.format(format, args);
        } else {
            return "App: " + "[T_" + Thread.currentThread().getName() + "]" + format;
        }
    }
    public static int i(String tag, String format, Object... args) {
        Log.i(tag, buildWholeMessage(format, args));

        return 0;
    }
    public static int e(String tag, String format, Object... args) {
        Log.e(tag, buildWholeMessage(format, args));

        return 0;
    }
}
