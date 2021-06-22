#import "AppDelegate.h"

#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#import "BridgeManager.h"
#import "HomeViewController.h"


@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

  // 在应用启动的时候加载 jsbundle 基础包
  [BridgeManager.instance loadBaseBundleWithLaunchOptions:launchOptions];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [[HomeViewController alloc] init];
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
}



@end
