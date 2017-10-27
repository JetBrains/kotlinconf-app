//
//  KVote+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KVote+CoreDataProperties.h"

@implementation KVote (CoreDataProperties)

+ (NSFetchRequest<KVote *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KVote"];
}

@dynamic rating;
@dynamic sessionId;

@end
