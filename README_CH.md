# react-native-dynamic-load

react-native dynamic load bundle from remote;(单例模式 React Native 分包加载)

## Support

- iOS/Android 动态加载 jsbundle/common bundle;
- iOS/Android 支持多 bundle 同时加载

## 实现逻辑

### 分包

如何进行分包？点击[ReactNative 分包方案介绍](https://blog.gaogangsever.cn/react/RNSDK%E5%8D%87%E7%BA%A7%E5%8F%8A%E5%88%86%E5%8C%85%E6%96%B9%E6%A1%88.html#%E9%97%AE%E9%A2%98-1-rn-%E5%A6%82%E4%BD%95%E8%BF%9B%E8%A1%8C%E5%88%86%E5%8C%85)

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
