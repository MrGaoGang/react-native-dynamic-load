package com.reactnativedynamic.core;

import androidx.annotation.Nullable;

import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.reactnativedynamic.core.utils.ScriptLoadUtil;
import com.reactnativedynamic.core.packages.CustomLogPackages;

import java.util.Arrays;
import java.util.List;

/**
 * Author: alexganggao
 * Created by: 2021/8/5
 * Description:
 */
public class DynamicReactNativeHost extends ReactNativeHost {

    protected DynamicReactNativeHost() {
        super(ReactAppRuntime.getApplication());
    }

    @Override
    public boolean getUseDeveloperSupport() {
        return ScriptLoadUtil.MULTI_DEBUG;//是否是debug模式
    }

    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                new CustomLogPackages()
        );
    }

    @Nullable
    @Override
    protected String getBundleAssetName() {
        return "common.android.bundle";
    }

    @Override
    protected String getJSMainModuleName() {
        return "";
    }

    private static class SingletonHostHolder {
        private static final DynamicReactNativeHost INSTANCE = new DynamicReactNativeHost();
    }

    public static final DynamicReactNativeHost getInstance() {
        return DynamicReactNativeHost.SingletonHostHolder.INSTANCE;
    }

}
