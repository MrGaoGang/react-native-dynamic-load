package com.reactnativedynamic.core.packages.client;

import java.util.Date;

public class TimeUtils {
    public static void logTime(String tag) {
        System.out.println("-------" + tag + "----------:" + new Date().getTime());
    }

    public static Long formatTime(Long time1, Long time2) {
        System.out.println("时间：time1 = " + time1 + ", time2 = " + time2);
        return Math.abs(time1 - time2);
    }
}
