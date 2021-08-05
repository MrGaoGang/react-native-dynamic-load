package com.reactnativedynamic;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.reactnativedynamic.core.ReactAppRuntime;
import com.reactnativedynamic.core.DynamicReactNativeHost;
import com.reactnativedynamic.core.ReactAppRuntime;


public class MainApplication extends Application {

    public static boolean isDynamic = false;


    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);
        ReactAppRuntime.init(this);
    }


}
