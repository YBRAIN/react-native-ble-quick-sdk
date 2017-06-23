//
//   NSYLog.m
//
//   Created by Eric S on 6/27/16.
//

#import <Foundation/Foundation.h>

#import "NSYLog.h"

void NSYLog(const char *file, int lineNumber, const char *functionName, NSString *format, ...)
{
    // Type to hold information about variable arguments.
    va_list ap;
    
    // Initialize a variable argument list.
    va_start (ap, format);
    
    // NSLog only adds a newline to the end of the NSLog format if
    // one is not already there.
    // Here we are utilizing this feature of NSLog()
    if (![format hasSuffix: @"\n"])
    {
        format = [format stringByAppendingString: @"\n"];
    }
    
    NSString *body = [[NSString alloc] initWithFormat:format arguments:ap];
    
    // End using variable argument list.
    va_end (ap);
    
    NSString *fileName = [[NSString stringWithUTF8String:file] lastPathComponent];
    
    fprintf(stderr, "[libRNBluetoothLE] (%s:%d) (%s)  %s",  [fileName UTF8String], lineNumber, functionName, [body UTF8String]);
    
    
    
}
