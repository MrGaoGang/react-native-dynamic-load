//
//  BridgeManager.m
//  ReactNativeDynamic
//
//  Created by mrgaogang on 2021/6/22.
//

#import "BridgeManager.h"
#import "RCTBridge+Extension.h"
@interface BridgeManager () <RCTBridgeDelegate>
@property(nonatomic, strong) NSMutableArray *pendingQueue;

@end
@implementation BridgeManager

- (instancetype)init {
  if (self = [super init]) {
    [self initManager];
  }
  return self;
}

+ (instancetype)instance {
  static BridgeManager *manager = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    manager = [[BridgeManager alloc] init];
  });
  return manager;
}

- (void)initManager {
  // success listener
  [[NSNotificationCenter defaultCenter]
   addObserver:self
   selector:@selector(onJSDidLoad:)
   name:RCTJavaScriptDidLoadNotification
   object:nil];
  // error listener
  [[NSNotificationCenter defaultCenter]
   addObserver:self
   selector:@selector(onJSLoadError:)
   name:RCTJavaScriptDidFailToLoadNotification
   object:nil];

  self.pendingQueue = [[NSMutableArray alloc] initWithCapacity:1];
}

- (void)loadBaseBundleWithLaunchOptions:(NSDictionary *)launchOptions {

  if (self.commonBridge == nil) {
    self.commonStatus = RNBundleLoadStatus_Init;
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:self
                                              launchOptions:launchOptions];
    self.commonBridge = bridge;
    self.commonStatus = RNBundleLoadStatus_Loading;
  }
}

#pragma mask - js listener
- (void)onJSDidLoad:(NSNotification *)notification {
  // base lib load success!
  NSLog(@"base bundle load success!");
  [self _baseBundleLoad:nil];
}

- (void)onJSLoadError:(NSNotification *)notification {
  // base lib load failed!
  NSLog(@"base bundle load faild!");
  [self _baseBundleLoad: [NSError new]];
}

// load business js bundle
- (void)loadBusinessBundle:(NSString *)path
                moduleName:(NSString *)moduleName
                  callback:(RNBusinessLoadCallback)callback {
  if (self.commonBridge == nil) {
    NSLog(@"basic jsbundle not loaded before business bundle!");
    return;
  }
  if (self.loadedScripts == nil) {
    self.loadedScripts = [NSMutableArray array];
  }

  NSString *jsCodeLocationBuz =
  [[NSBundle mainBundle] URLForResource:path withExtension:@"bundle"]
  .path;
  NSError *error = nil;
  // you can replace to you own file location
  NSData *sourceBuz = [NSData dataWithContentsOfFile:jsCodeLocationBuz
                                             options:NSDataReadingMappedIfSafe
                                               error:&error];

  if (error == nil) { // load source success
    RNBusinessLoadCallback blk = ^(BOOL successed) {
      if (successed) {
        if ([self.loadedScripts indexOfObject:moduleName] == NSNotFound) {
          [self.loadedScripts addObject:moduleName];
          [self.commonBridge.batchedBridge executeSourceCode:sourceBuz sync:NO];
        }else{
          // has loaded
          NSLog(@"bundle %@ has loaded",path);
        }
        callback(YES);

      } else {
        callback(successed);
      }
    };

    if (self.commonStatus == RNBundleLoadedStatus_Successed) {
      blk(YES);
    } else { // wait for base bundle load success
      [self.pendingQueue addObject:blk];
    }
  } else {
    callback(NO);
  }


}

- (void)loadScriptWithBridge:(RCTBridge *)bridge
                        path:(NSString *)path
                  moduleName:(NSString *)moduleName
                    callback:(RNBusinessLoadCallback)callback {
}

#pragma mark - Queue

- (void)_processPendingQueue {

  if (self.commonStatus == RNBundleLoadedStatus_Successed) {
    for (RNBusinessLoadCallback blk in self.pendingQueue) {
      blk(YES);
    }
  } else if (self.commonStatus ==
             RNBundleLoadStatus_Failed) { // in case load failed
    for (RNBusinessLoadCallback blk in self.pendingQueue) {
      blk(NO);
    }
  }

  [self.pendingQueue removeAllObjects]; // clean up
}

- (void)_baseBundleLoad:(NSError *)error {

  static int retry = 0;
  if (error && retry > 0) {
    retry--;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 0.5 * NSEC_PER_SEC),
                   dispatch_get_main_queue(), ^{
      [self.commonBridge reload];
    });
  } else {
    self.commonStatus =
    error ? RNBundleLoadStatus_Failed : RNBundleLoadedStatus_Successed;
    [self _processPendingQueue];
  }
}

#pragma mark - RCTBridgeDelegate

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge {
  NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"common.ios"
                                                         ofType:@"bundle"];
  return [NSURL fileURLWithPath:bundlePath];
}

// load base js bundle
- (void)loadSourceForBridge:(RCTBridge *)bridge
                 onProgress:(RCTSourceLoadProgressBlock)onProgress
                 onComplete:(RCTSourceLoadBlock)loadCallback {

  __weak typeof(self) weakSelf = self;

  [RCTJavaScriptLoader loadBundleAtURL:[self sourceURLForBridge:bridge]
                            onProgress:onProgress
                            onComplete:^(NSError *error, RCTSource *source) {
    NSLog(@"common lib src did load, error:%@",
          error.localizedDescription);
    loadCallback(error, source);
    [weakSelf _baseBundleLoad:error];
  }];
}


- (void)dealloc {
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}
@end
