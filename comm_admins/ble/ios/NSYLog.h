//
//   NSYLog.h
//
//   Created by Eric S  on 6/27/16.
//

#ifndef NSYLog_h
#define NSYLog_h

#import <Foundation/Foundation.h>

#ifdef DEBUG
#define NSLog(args...) NSYLog(__FILE__,__LINE__,__PRETTY_FUNCTION__,args);
#else
#define NSLog(x...)
#endif

void NSYLog(const char *file, int lineNumber, const char *functionName, NSString *format, ...);


#endif /* NSYLog_h */
