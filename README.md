# react-native-dynamic-load
[中文](./README_CH.md)

react-native dynamic load bundle from remote;
## Support

- iOS/Android dynamic load jsbundle/common bundle;
- iOS/Android supports simultaneous loading of multiple bundles

## Implementation logic

### Split Bundle

How to split bundle ? Click [ReactNative Subcontracting Program Introduction](https://dev.to/mrgaogang/react-native-sdk-upgrade-issues-and-split-jsbundle-46k9)

```bash

# ios 

npm run build:ios

# android

npm run build:android

```

It will be automatically split bundle. If you want the packaged product to be a digital type, you can set `moduleIdByIndex=true` in `compile/metro-base.js`

### iOS client dynamic load

```objc
// Load the jsbundle basic package when the application starts
[BridgeManager.instance loadBaseBundleWithLaunchOptions:launchOptions];

// Load business packages when needed
// Here is just the way to load the local bundle. If it is online, you can download it using http first and then load it locally
[BridgeManager.instance
    loadBusinessBundle:@"business.ios"
            moduleName:@"ReactNativeDynamic"
              callback:^(BOOL succeed) {
                if (succeed) {
                  RCTRootView *rootView = [[RCTRootView alloc]
                         initWithBridge:BridgeManager.instance.commonBridge
                             moduleName:@"ReactNativeDynamic"
                      initialProperties:nil];
                  self.view = rootView;
                }
  NSLog(@"%d",succeed);
              }];
```

### Android client dynamic load

```java
//  Load the jsbundle basic package when the application starts
SoLoader.init(this, /* native exopackage */ false);
ReactAppRuntime.init(this);

// your activity
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


```
