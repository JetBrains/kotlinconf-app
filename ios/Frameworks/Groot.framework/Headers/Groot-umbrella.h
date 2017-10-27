#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "Groot.h"
#import "GRTError.h"
#import "GRTJSONSerialization.h"
#import "GRTManagedStore.h"
#import "NSValueTransformer+Groot.h"

FOUNDATION_EXPORT double GrootVersionNumber;
FOUNDATION_EXPORT const unsigned char GrootVersionString[];

