package com.reactnativedynamic.core.bridge;

// Copyright (c) Facebook, Inc. and its affiliates.

// This source code is licensed under the MIT license found in the
// LICENSE file in the root directory of this source tree.


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactDelegate;
import com.facebook.react.ReactFragmentActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.PermissionListener;
import com.reactnativedynamic.core.DynamicReactNativeHost;

/**
 * Delegate class for {@link ReactActivity} and {@link ReactFragmentActivity}. You can subclass this
 * to provide custom implementations for e.g. {@link #getReactNativeHost()}, if your Application
 * class doesn't implement {@link ReactApplication}.
 */
public class ReactActivityDelegate {

    private final @Nullable
    Activity mActivity;
    private final @Nullable
    String mMainComponentName;

    private @Nullable
    PermissionListener mPermissionListener;
    private @Nullable
    Callback mPermissionsCallback;
    private ReactDelegate mReactDelegate;

    @Deprecated
    public ReactActivityDelegate(Activity activity, @Nullable String mainComponentName) {
        mActivity = activity;
        mMainComponentName = mainComponentName;
    }

    public ReactActivityDelegate(ReactActivity activity, @Nullable String mainComponentName) {
        mActivity = activity;
        mMainComponentName = mainComponentName;
    }

    public @Nullable
    Bundle getLaunchOptions() {
        return null;
    }

    public ReactRootView createRootView() {
        return new ReactRootView(getContext());
    }

    /**
     * Get the {@link ReactNativeHost} used by this app. By default, assumes {@link
     * Activity#getApplication()} is an instance of {@link ReactApplication} and calls {@link
     * ReactApplication#getReactNativeHost()}. Override this method if your application class does not
     * implement {@code ReactApplication} or you simply have a different mechanism for storing a
     * {@code ReactNativeHost}, e.g. as a static field somewhere.
     */
    public ReactNativeHost getReactNativeHost() {
        return DynamicReactNativeHost.getInstance();
    }

    public ReactInstanceManager getReactInstanceManager() {
        return mReactDelegate.getReactInstanceManager();
    }

    public String getMainComponentName() {
        return mMainComponentName;
    }

    public void onCreate(Bundle savedInstanceState) {
        String mainComponentName = getMainComponentName();
        mReactDelegate =
                new ReactDelegate(
                        getPlainActivity(), getReactNativeHost(), mainComponentName, getLaunchOptions()) {
                    @Override
                    public ReactRootView createRootView() {
                        return ReactActivityDelegate.this.createRootView();
                    }
                };
        if (mMainComponentName != null) {
            loadApp(mainComponentName);
        }
    }

    public void onCreateReactDelegate(Bundle savedInstanceState) {
        String mainComponentName = getMainComponentName();
        if (mReactDelegate == null) {
            mReactDelegate =
                    new ReactDelegate(
                            getPlainActivity(), getReactNativeHost(), mainComponentName, getLaunchOptions()) {
                        @Override
                        public ReactRootView createRootView() {
                            return ReactActivityDelegate.this.createRootView();
                        }
                    };
        }


    }

    public void loadApp(String appKey) {
        mReactDelegate.loadApp(appKey);
        getPlainActivity().setContentView(mReactDelegate.getReactRootView());
    }

    public void onPause() {
        mReactDelegate.onHostPause();
    }

    public void onResume() {
        mReactDelegate.onHostResume();

        if (mPermissionsCallback != null) {
            mPermissionsCallback.invoke();
            mPermissionsCallback = null;
        }
    }

    public void onDestroy() {
        mReactDelegate.onHostDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mReactDelegate.onActivityResult(requestCode, resultCode, data, true);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            event.startTracking();
            return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mReactDelegate.shouldShowDevMenuOrReload(keyCode, event);
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
            return true;
        }
        return false;
    }

    public boolean onBackPressed() {
        return mReactDelegate.onBackPressed();
    }

    public boolean onNewIntent(Intent intent) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onNewIntent(intent);
            return true;
        }
        return false;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onWindowFocusChange(hasFocus);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(
            String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        getPlainActivity().requestPermissions(permissions, requestCode);
    }

    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults) {
        mPermissionsCallback =
                new Callback() {
                    @Override
                    public void invoke(Object... args) {
                        if (mPermissionListener != null
                                && mPermissionListener.onRequestPermissionsResult(
                                requestCode, permissions, grantResults)) {
                            mPermissionListener = null;
                        }
                    }
                };
    }

    public Context getContext() {
        return Assertions.assertNotNull(mActivity);
    }

    public Activity getPlainActivity() {
        return ((Activity) getContext());
    }
}

