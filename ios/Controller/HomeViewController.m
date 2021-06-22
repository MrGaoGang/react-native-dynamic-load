//
//  HomeViewController.m
//  ReactNativeDynamic
//
//  Created by mrgaogang on 2021/6/22.
//

#import "BridgeManager.h"
#import "HomeViewController.h"
#import <React/RCTRootView.h>

@interface HomeViewController ()

@end

@implementation HomeViewController

- (void)viewDidLoad {
[super viewDidLoad];
// 此处只是使用加载本地的bundle的方式，如果是在线的方式，可以先使用http下载然后加载本地
[BridgeManager.instance
    loadBusinessBundle:@"buss.ios"
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
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little
preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
// Get the new view controller using [segue destinationViewController].
// Pass the selected object to the new view controller.
}
*/

@end
