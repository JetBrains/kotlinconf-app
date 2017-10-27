//
//  KLink+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KLink+CoreDataProperties.h"

@implementation KLink (CoreDataProperties)

+ (NSFetchRequest<KLink *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KLink"];
}

@dynamic linkType;
@dynamic title;
@dynamic url;

@end
