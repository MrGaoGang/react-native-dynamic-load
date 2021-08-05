/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.reactnativedynamic.core;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.reactnativedynamic.core.bridge.ReactActivityDelegate;
import com.reactnativedynamic.core.utils.FileUtils;
import com.reactnativedynamic.core.utils.LoadScriptListener;
import com.reactnativedynamic.core.model.RnBundle;
import com.reactnativedynamic.core.utils.ScriptLoadUtil;
import com.reactnativedynamic.core.utils.UpdateProgressListener;
import com.reactnativedynamic.core.packages.client.TimeRecord;


import java.io.File;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * 异步加载业务bundle的activity
 */
public abstract class AsyncReactActivity extends androidx.fragment.app.FragmentActivity
        implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    public enum ScriptType {ASSET, FILE, NETWORK}

    private final ReactActivityDelegate mDelegate;
    protected boolean bundleLoaded = false;
    private AlertDialog mProgressDialog;

    protected AsyncReactActivity() {
        mDelegate = createReactActivityDelegate();
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     * e.g. "MoviesApp"
     */
    final private @Nullable
    String getMainComponentNameInner() {
        if (!bundleLoaded &&
                getBundle().scriptType == ScriptType.NETWORK) {
            return null;
        }
        return getBundle().appName;
    }


    /**
     * Called at construction time, override if you have a custom delegate implementation.
     */
    protected ReactActivityDelegate createReactActivityDelegate() {
        return new ReactActivityDelegate(this, getMainComponentNameInner());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimeRecord.mDynamicLog.startActivityTime = new Date().getTime();

        final ReactInstanceManager manager = DynamicReactNativeHost.getInstance().getReactInstanceManager();
        mDelegate.onCreateReactDelegate(null);

        if (!manager.hasStartedCreatingInitialContext()
                || ScriptLoadUtil.getCatalystInstance(getReactNativeHost()) == null) {
            //由于下面是异步执行，所以需要在此处创建ReactDelegate

            manager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
                @Override
                public void onReactContextInitialized(ReactContext context) {
                    loadScript(new LoadScriptListener() {
                        @Override
                        public void onLoadComplete(boolean success, String scriptPath) {
                            bundleLoaded = success;
                            TimeRecord.mDynamicLog.resLoadTime = new Date().getTime();
                            if (success)
                                runApp(scriptPath);
                        }
                    });
                    manager.removeReactInstanceEventListener(this);
                }
            });
            DynamicReactNativeHost.getInstance().getReactInstanceManager().createReactContextInBackground();
        } else {
            loadScript(new LoadScriptListener() {
                @Override
                public void onLoadComplete(boolean success, String scriptPath) {
                    bundleLoaded = success;
                    TimeRecord.mDynamicLog.resLoadTime = new Date().getTime();
                    if (success)
                        runApp(scriptPath);
                }
            });
        }

    }

    protected abstract RnBundle getBundle();

    protected void runApp(String scriptPath) {
        if (scriptPath != null) {
            scriptPath = "file://" + scriptPath.substring(0, scriptPath.lastIndexOf(File.separator) + 1);
        }
        final String path = scriptPath;
        final RnBundle bundle = getBundle();
        final ReactInstanceManager reactInstanceManager = DynamicReactNativeHost.getInstance().getReactInstanceManager();
        if (bundle.scriptType == ScriptType.NETWORK) {//如果是网络加载的话，此时正在子线程
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ScriptLoadUtil.setJsBundleAssetPath(
                            reactInstanceManager.getCurrentReactContext(),
                            path);
                    mDelegate.loadApp(getMainComponentNameInner());
                }
            });
        } else {//主线程运行
            ScriptLoadUtil.setJsBundleAssetPath(
                    reactInstanceManager.getCurrentReactContext(),
                    path);
            mDelegate.loadApp(getMainComponentNameInner());
        }
    }

    protected void loadScript(final LoadScriptListener loadListener) {
        final RnBundle bundle = getBundle();
        /** all buz module is loaded when in debug mode*/
        if (ScriptLoadUtil.MULTI_DEBUG) {//当设置成debug模式时，所有需要的业务代码已经都加载好了
            loadListener.onLoadComplete(true, null);
            return;
        }
        ScriptType pathType = bundle.scriptType;
        String scriptPath = bundle.scriptUrl;
        final CatalystInstance instance = ScriptLoadUtil.getCatalystInstance(getReactNativeHost());
        if (pathType == ScriptType.ASSET) {
            ScriptLoadUtil.loadScriptFromAsset(getApplicationContext(), instance, scriptPath, false);
            loadListener.onLoadComplete(true, null);
        } else if (pathType == ScriptType.FILE) {
            File scriptFile = new File(getApplicationContext().getFilesDir()
                    + File.separator +/*ScriptLoadUtil.REACT_DIR+File.separator+*/scriptPath);
            scriptPath = scriptFile.getAbsolutePath();
            ScriptLoadUtil.loadScriptFromFile(scriptPath, instance, scriptPath, false);
            loadListener.onLoadComplete(true, scriptPath);
        } else if (pathType == ScriptType.NETWORK) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Loading jsBundle");
            dialogBuilder.setCancelable(false);
            final TextView tvv = new TextView(this);
            tvv.setText("conneting");//由于demo中把文件放在了github上，所以http建立连接要花好几秒时间
            tvv.setTextColor(Color.BLACK);
            tvv.setGravity(Gravity.CENTER);
            tvv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialogBuilder.setView(tvv);
            mProgressDialog = dialogBuilder.create();
            mProgressDialog.show();
            //由于downloadRNBundle里面的md5参数由组件名代替了，实际开发中需要用到md5校验的需要自己修改
            FileUtils.downloadRNBundle(this.getApplicationContext(), scriptPath, bundle.appName, new UpdateProgressListener() {
                @Override
                public void updateProgressChange(final int precent) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tvv != null) {
                                tvv.setText(String.valueOf(precent));
                            }
                        }
                    });
                }

                @Override
                public void complete(boolean success) {
                    if (mProgressDialog != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        });
                    }
                    if (!success) {
                        loadListener.onLoadComplete(false, null);
                        return;
                    }
                    TimeRecord.mDynamicLog.resDownloadTime = new Date().getTime();
                    String info = FileUtils.getCurrentPackageMd5(getApplicationContext());
                    String bundlePath = FileUtils.getPackageFolderPath(getApplicationContext(), info);
                    System.out.println("是否已经下载完成了 ======== " + bundlePath);

                    String jsBundleFilePath = FileUtils.appendPathComponent(bundlePath, bundle.scriptPath);
                    System.out.println("jsBundleFilePath ======== " + jsBundleFilePath);

                    File bundleFile = new File(jsBundleFilePath);
                    if (bundleFile != null && bundleFile.exists()) {
                        System.out.println("文件存在 = " + success);
                        ScriptLoadUtil.loadScriptFromFile(jsBundleFilePath, instance, jsBundleFilePath, false);
                    } else {
                        success = false;
                    }
                    loadListener.onLoadComplete(success, jsBundleFilePath);
                }
            });
        }
    }

    protected void initView() {
        mDelegate.onCreate(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDelegate.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelegate.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDelegate.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mDelegate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mDelegate.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mDelegate.onKeyLongPress(keyCode, event) || super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!mDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (!mDelegate.onNewIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void requestPermissions(
            String[] permissions,
            int requestCode,
            PermissionListener listener) {
        mDelegate.requestPermissions(permissions, requestCode, listener);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        mDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected final ReactNativeHost getReactNativeHost() {
        return mDelegate.getReactNativeHost();
    }

    protected final ReactInstanceManager getReactInstanceManager() {
        return mDelegate.getReactInstanceManager();
    }

    protected final void loadApp(String appKey) {
        mDelegate.loadApp(appKey);
    }
}
