package com.reactnativedynamic;

import com.reactnativedynamic.core.AsyncReactActivity;
import com.reactnativedynamic.core.model.RnBundle;


public class MainActivity  extends AsyncReactActivity {

  @Override
  protected RnBundle getBundle(){
    RnBundle bundle = new RnBundle();
    bundle.scriptType = ScriptType.ASSET;
    bundle.scriptPath = "business.android.bundle";
    bundle.scriptUrl = "business.android.bundle";
    bundle.appName = "ReactNativeDynamic";
    return bundle;
  }
}
