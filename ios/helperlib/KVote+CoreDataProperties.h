//
//  KVote+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KVote+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KVote (CoreDataProperties)

+ (NSFetchRequest<KVote *> *)fetchRequest;

@property (nonatomic) int32_t rating;
@property (nullable, nonatomic, copy) NSString *sessionId;

@end

NS_ASSUME_NONNULL_END
