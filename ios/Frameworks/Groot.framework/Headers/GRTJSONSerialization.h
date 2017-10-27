// GRTJSONSerialization.h
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
 Converts JSON dictionaries and JSON arrays to and from Managed Objects.
 */
@interface GRTJSONSerialization : NSObject

/**
 Creates or updates a set of managed objects from JSON data.
 
 @param entityName The name of an entity.
 @param data A data object containing JSON data.
 @param context The context into which to fetch or insert the managed objects.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 
 @return An array of managed objects, or `nil` if an error occurs.
 */
+ (nullable NSArray<__kindof NSManagedObject *> *)objectsWithEntityName:(NSString *)entityName
                                                           fromJSONData:(NSData *)data
                                                              inContext:(NSManagedObjectContext *)context
                                                                  error:(NSError * __nullable * __nullable)error;

/**
 Creates or updates a managed object from a JSON dictionary.
 
 This method converts the specified JSON dictionary into a managed object of a given entity.
 
 @param entityName The name of an entity.
 @param JSONDictionary A dictionary representing JSON data. This should match the format returned
                       by `NSJSONSerialization`.
 @param context The context into which to fetch or insert the managed objects.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 
 @return A managed object, or `nil` if an error occurs.
 */
+ (nullable __kindof NSManagedObject *)objectWithEntityName:(NSString *)entityName
                                         fromJSONDictionary:(NSDictionary *)JSONDictionary
                                                  inContext:(NSManagedObjectContext *)context
                                                      error:(NSError * __nullable * __nullable)error;

/**
 Creates or updates a set of managed objects from a JSON array.
 
 @param entityName The name of an entity.
 @param JSONArray An array representing JSON data. This should match the format returned by
                  `NSJSONSerialization`.
 @param context The context into which to fetch or insert the managed objects.
 @param error If an error occurs, upon return contains an NSError object that describes the problem.
 
 @return An array of managed objects, or `nil` if an error occurs.
 */
+ (nullable NSArray<__kindof NSManagedObject *> *)objectsWithEntityName:(NSString *)entityName
                                                          fromJSONArray:(NSArray *)JSONArray
                                                              inContext:(NSManagedObjectContext *)context
                                                                  error:(NSError * __nullable * __nullable)error;

/**
 Converts a managed object into a JSON representation.
 
 @param object The managed object to use for JSON serialization.

 @return A JSON dictionary.
 */
+ (NSDictionary *)JSONDictionaryFromObject:(NSManagedObject *)object;

/**
 Converts an array of managed objects into a JSON representation.
 
 @param objects The array of managed objects to use for JSON serialization.
 
 @return A JSON array.
 */
+ (NSArray *)JSONArrayFromObjects:(NSArray *)objects;

@end

NS_ASSUME_NONNULL_END
