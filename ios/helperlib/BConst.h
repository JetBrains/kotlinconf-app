//
//  BConst.h
//  helperlib
//
//  Created by Yan Zhulanow on 10/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>

@interface BWork : NSObject
- (void)work;
@end

@interface BResponse : NSObject
- (void)completedWithData:(NSData*)data response:(NSURLResponse*)response error:(NSError*)error;
@end

@interface BConst : NSObject

- (void)logText:(NSString*)text;
- (void)performInContext:(NSManagedObjectContext*)context task:(BWork*)task;
- (void)performAndWaitInContext:(NSManagedObjectContext*)context task:(BWork*)task;
- (void)mainAsyncWithTask:(BWork*)task;
- (void)mainAsyncAfterWithTask:(NSInteger)msec task:(BWork*)task;
- (NSURLSessionDataTask*)dataTaskWithSession:(NSURLSession*)session request:(NSURLRequest*)request handler:(BResponse*)handler;

@end
