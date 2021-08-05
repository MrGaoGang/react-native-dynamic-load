package com.reactnativedynamic.core;

import android.app.Application;

/**
 * Author: alexganggao
 * Created by: 2021/8/5
 * Description:
 */


public class ReactAppRuntime {
    private static class SingletonHolder {
        private static final ReactAppRuntime INSTANCE = new ReactAppRuntime();
    }

    private ReactAppRuntime() {
    }

    private Application mApplication;

    public static void init(Application application) {
        SingletonHolder.INSTANCE.mApplication = application;
        DynamicReactNativeHost.getInstance();
    }

    public static final ReactAppRuntime getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static Application getApplication() {
        return getInstance().mApplication;
    }
}
