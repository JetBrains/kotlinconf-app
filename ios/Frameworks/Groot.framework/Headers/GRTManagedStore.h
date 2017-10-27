// GRTManagedStore.h
//
// Copyright (c) 2014-2016 Guillermo Gonzalez
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

#import <CoreData/CoreData.h>

NS_ASSUME_NONNULL_BEGIN

/**
 Manages a Core Data stack.
 */
@interface GRTManagedStore : NSObject

/**
 The persistent store coordinator.
 */
@property (strong, nonatomic, readonly) NSPersistentStoreCoordinator *persistentStoreCoordinator;

/**
 The managed object model.
 */
@property (strong, nonatomic, readonly) NSManagedObjectModel *managedObjectModel;

/**
 The URL for this managed store.
 */
@property (copy, nonatomic, readonly) NSURL *URL;

/**
 Initializes the receiver with the specified location and managed object model.
 
 This is the designated initializer.
 
 @param URL The file location of the store. If `nil` the persistent store will be created in memory.
 @param model The managed object model.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 */
- (nullable instancetype)initWithURL:(nullable NSURL *)URL model:(NSManagedObjectModel *)managedObjectModel error:(NSError * __nullable * __nullable)error NS_DESIGNATED_INITIALIZER;

/**
 Initializes a managed store that will persist its data in a discardable cache file.
 
 @param cacheName The name of the cache file.
 @param model The managed object model.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 */
- (nullable instancetype)initWithCacheName:(NSString *)cacheName model:(NSManagedObjectModel *)managedObjectModel error:(NSError * __nullable * __nullable)error;

/**
 Initializes a managed store that will persist its data in memory.
 
 @param model The managed object model.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 */
- (nullable instancetype)initWithModel:(NSManagedObjectModel *)managedObjectModel error:(NSError * __nullable * __nullable)error;

/**
 Creates and returns a managed store that will persist its data at a given location.
 
 @param URL The file location of the store.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 */
+ (nullable instancetype)storeWithURL:(NSURL *)URL error:(NSError * __nullable * __nullable)error;

/**
 Creates and returns a managed store that will persist its data in a discardable cache file.
 
 @param cacheName The file name.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 */
+ (nullable instancetype)storeWithCacheName:(NSString *)cacheName error:(NSError * __nullable * __nullable)error;

/**
 Creates and returns a managed object context for this store.
 */
- (NSManagedObjectContext *)contextWithConcurrencyType:(NSManagedObjectContextConcurrencyType)concurrencyType;

@end

NS_ASSUME_NONNULL_END
