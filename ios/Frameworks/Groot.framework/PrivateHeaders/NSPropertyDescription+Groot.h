// NSPropertyDescription+Groot.h
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

@interface NSPropertyDescription (Groot)

/**
 The JSON key path.
 */
- (nullable NSString *)grt_JSONKeyPath;

/**
 Returns `true` if this property should participate in the JSON serialization process.
 */
- (BOOL)grt_JSONSerializable;

/**
 Returns the untransformed raw value for this property in a given JSON object.
 */
- (nullable id)grt_rawValueInJSONDictionary:(NSDictionary *)object;

@end

NS_ASSUME_NONNULL_END
