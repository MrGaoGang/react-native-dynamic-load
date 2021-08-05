# react-native-dynamic-load

react-native dynamic load bundle from remote;(单例模式 React Native 分包加载)

## Support

- iOS 动态加载 jsbundle/common bundle;
- iOS 支持多 bundle 同时加载

## 实现逻辑

### 分包

将`polyfill`和`node_modules`部分内置到客户端`base.ios.bundle`,其余的业务逻辑为`buss.ios.bundle`。此处可以根据自己的个性需求选择那些需要内置。

```js
function processModuleFilter(type) {
  return module => {
    if (type === 'ALL') {
      return true;
    } else if (type === 'BASE') {
      const projectName = __dirname.substr(__dirname.lastIndexOf('/') + 1);
      if (module.path.indexOf('__prelude__') !== -1) {
        return true;
      }
      if (module.path.indexOf(`${projectName}/node_modules`) !== -1) {
        return true;
      } else {
        return false;
      }
    } else if (type === 'BUSINESS') {
      const projectName = __dirname.substr(__dirname.lastIndexOf('/') + 1);
      if (module.path.indexOf('__prelude__') !== -1) {
        return false;
      }
      if (module.path.indexOf(`${projectName}/node_modules`) !== -1) {
        return false;
      } else {
        return true;
      }
    }
  };
}
```

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