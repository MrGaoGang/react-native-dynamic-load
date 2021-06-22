//
//  RCTBridge+Private.h
//  ReactNativeDynamic
//
//  Created by mrgaogang on 2021/6/22.
//

#import <React/RCTBridge+Private.h>

NS_ASSUME_NONNULL_BEGIN

@interface RCTBridge (Extension)
-(void)executeSourceCode:(NSData *)sourceCode sync:(BOOL)sync;
@end

NS_ASSUME_NONNULL_END
