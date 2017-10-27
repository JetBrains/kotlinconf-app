//
//  KAll+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KAll+CoreDataProperties.h"

@implementation KAll (CoreDataProperties)

+ (NSFetchRequest<KAll *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KAll"];
}

@dynamic category;
@dynamic room;
@dynamic session;
@dynamic speaker;
@dynamic vote;
@dynamic favorites;

@end
