package com.reactnativedynamic;

import com.reactnativedynamic.core.DynamicReactActivity;
import com.reactnativedynamic.core.model.RnBundle;


public class MainActivity  extends DynamicReactActivity {

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
