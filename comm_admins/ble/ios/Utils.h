#import <Foundation/Foundation.h>
#import "NSYLog.h"

@interface Utils : NSObject 


+ (NSString *)stringFromHex : (NSData*) hexVal;
+ (NSArray *)numberArrayFromHex : (NSData*) hexVal;

@end
