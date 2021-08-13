# react-native-dynamic-load

react-native dynamic load bundle from remote;(单例模式 React Native 分包加载)

## Support

- iOS 动态加载 jsbundle/common bundle;
- iOS 支持多 bundle 同时加载

## 实现逻辑

### 分包

将自己需要打入到公共包里面的模块，填写到`common-entry.js`中;

```bash

# ios运行

npm run build:ios

# android运行

npm run build:android

```

会自动进行分包，如果想要打包后的产物为数字类型，则在`compile/metro-base.js`中设置`moduleIdByIndex=true`即可

### iOS 客户端动态加载

```objc
// 在应用启动的时候加载 jsbundle 基础包
[BridgeManager.instance loadBaseBundleWithLaunchOptions:launchOptions];

// 在需要的时候加载业务包
// 此处只是使用加载本地的bundle的方式，如果是在线的方式，可以先使用http下载然后加载本地
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

### Android 客户端动态加载

```java
// 应用启动的时候
SoLoader.init(this, /* native exopackage */ false);
ReactAppRuntime.init(this);

// 你的activity
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
