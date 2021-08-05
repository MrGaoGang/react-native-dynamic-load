package com.reactnativedynamic.core.packages.client;


import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.reactnativedynamic.MainApplication;

import java.util.Date;
import java.util.HashMap;

public class LogModule extends ReactContextBaseJavaModule {
    public LogModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "LogModule";
    }

    @ReactMethod
    public void log(String type) {
        System.out.println("type = " + type);
        if (!MainApplication.isDynamic) {
            switch (type) {
                case "mounted":
                    TimeRecord.mStaticLog.componentMountedTime = new Date().getTime();
                    break;
                case "render":
                    TimeRecord.mStaticLog.startRenderTime = new Date().getTime();
                    break;
                case "constructor":
                    TimeRecord.mStaticLog.initTime = new Date().getTime();
                    break;

                case "updated":
                    TimeRecord.mStaticLog.updatedTime = new Date().getTime();
                    break;
            }
        } else {
            switch (type) {
                case "mounted":
                    TimeRecord.mDynamicLog.componentMountedTime = new Date().getTime();
                    break;
                case "render":
                    TimeRecord.mDynamicLog.startRenderTime = new Date().getTime();
                    break;
                case "constructor":
                    TimeRecord.mDynamicLog.initTime = new Date().getTime();
                    break;
                case "updated":
                    TimeRecord.mDynamicLog.updatedTime = new Date().getTime();
                    break;
            }
        }

    }


    @ReactMethod
    public void showDynamicTimes(Callback callback) {
        boolean isDynamic =MainApplication.isDynamic;
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("&user button click time", isDynamic ? TimeRecord.mDynamicLog.clickTime : TimeRecord.mStaticLog.clickTime);
        hashMap.put("&init create view time", isDynamic ? TimeRecord.mDynamicLog.initCreateViewTime : TimeRecord.mStaticLog.initCreateViewTime);
        hashMap.put("&constructor time", isDynamic ? TimeRecord.mDynamicLog.initTime : TimeRecord.mStaticLog.initTime);
        hashMap.put("&init create view end time", isDynamic ? TimeRecord.mDynamicLog.createViewEndTime : TimeRecord.mStaticLog.createViewEndTime);
        hashMap.put("&start app time", isDynamic ? TimeRecord.mDynamicLog.startAppTime : TimeRecord.mStaticLog.startAppTime);
        hashMap.put("&render end time", isDynamic ? TimeRecord.mDynamicLog.startRenderTime : TimeRecord.mStaticLog.startRenderTime);
        hashMap.put("&mounted time", isDynamic ? TimeRecord.mDynamicLog.componentMountedTime : TimeRecord.mStaticLog.componentMountedTime);
        hashMap.put("&res download end time", isDynamic ? TimeRecord.mDynamicLog.resDownloadTime : 0);
        hashMap.put("&resource load time", isDynamic ? TimeRecord.mDynamicLog.resLoadTime : TimeRecord.mStaticLog.resLoadTime);

        hashMap.put("资源下载时间", isDynamic ? TimeUtils.formatTime(TimeRecord.mDynamicLog.resDownloadTime, TimeRecord.mDynamicLog.clickTime) : 0);
        hashMap.put("分包加载时间", isDynamic ? TimeUtils.formatTime(TimeRecord.mDynamicLog.resLoadTime, TimeRecord.mDynamicLog.resDownloadTime) : 0);
        hashMap.put("RN应用启动耗时", isDynamic ? TimeUtils.formatTime(TimeRecord.mDynamicLog.initTime, TimeRecord.mDynamicLog.resLoadTime) : TimeUtils.formatTime(TimeRecord.mStaticLog.initTime, TimeRecord.mStaticLog.startActivityTime));
        hashMap.put("首屏视图渲染时间", isDynamic ? TimeUtils.formatTime(TimeRecord.mDynamicLog.updatedTime!=0?TimeRecord.mDynamicLog.updatedTime:TimeRecord.mDynamicLog.startRenderTime, TimeRecord.mDynamicLog.clickTime) : TimeUtils.formatTime(TimeRecord.mStaticLog.updatedTime, TimeRecord.mStaticLog.componentMountedTime));

        callback.invoke(new Gson().toJson(hashMap));
    }
}
