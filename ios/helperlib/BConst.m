//
//  BConst.m
//  helperlib
//
//  Created by Yan Zhulanow on 10/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//

#import "BConst.h"

@interface BConst ()

@end

@interface BWork ()

@end

@interface BResponse ()

@end

@implementation BWork
- (void)work {}
@end

@implementation BResponse
- (void)completedWithData:(NSData*)data response:(NSURLResponse*)response error:(NSError*)error {}
@end

@implementation BConst

- (void)logText:(NSString*)text {
    NSLog(text);
}

- (void)performInContext:(NSManagedObjectContext*)context task:(BWork*)task {
    [context performBlock:^{
        [task work];
    }];
}

- (void)performAndWaitInContext:(NSManagedObjectContext*)context task:(BWork*)task {
    [context performBlockAndWait:^{
        [task work];
    }];
}

- (void)mainAsyncWithTask:(BWork*)task {
    dispatch_async(dispatch_get_main_queue(), ^{
        [task work];
    });
}

- (void)mainAsyncAfterWithTask:(NSInteger)msec task:(BWork*)task {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, msec * NSEC_PER_MSEC), dispatch_get_main_queue(), ^{
        [task work];
    });
}

- (NSURLSessionDataTask*)dataTaskWithSession:(NSURLSession*)session request:(NSURLRequest*)request handler:(BResponse*)handler {
    return [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        [handler completedWithData:data response:response error:error];
    }];
}

@end
