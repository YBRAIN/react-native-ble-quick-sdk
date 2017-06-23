#import "Utils.h"

@implementation Utils
    
+ (NSArray *)numberArrayFromHex : (NSData*) hexVal {
    
    const unsigned char *dataBuffer = (const unsigned char *)[hexVal bytes];
    
    NSUInteger          dataLength  = [hexVal length];
    NSMutableArray* nsarr = [[NSMutableArray alloc] initWithCapacity:1];
    
    
    for (int i = 0; i < dataLength; ++i)
    {
        unsigned char val = (unsigned char)dataBuffer[i];
        [nsarr addObject:[NSNumber numberWithUnsignedChar:val]];
    }
    
    NSLog(@"numberArrayFromHex : %@", [nsarr componentsJoinedByString:@","]);
    
    return nsarr;
}
    
    
    
    // Borrowed from innoveit/react-native-ble-manager
+ (NSString *)stringFromHex : (NSData*) hexVal {
    
    
    const unsigned char *dataBuffer = (const unsigned char *)[hexVal bytes];
    
    if (!dataBuffer)
    return [NSString string];
    
    NSUInteger          dataLength  = [hexVal length];
    NSMutableString     *hexString  = [NSMutableString stringWithCapacity:(dataLength * 2)];
    
    for (int i = 0; i < dataLength; ++i)
    [hexString appendString:[NSString stringWithFormat:@"%02x", (unsigned int)dataBuffer[i]]];
    
    NSLog(@"stringFromHex is  %@", hexString);
    return [NSString stringWithString:hexString];
}
    
    
@end
