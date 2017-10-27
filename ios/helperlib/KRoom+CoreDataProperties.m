//
//  KRoom+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KRoom+CoreDataProperties.h"

@implementation KRoom (CoreDataProperties)

+ (NSFetchRequest<KRoom *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KRoom"];
}

@dynamic id;
@dynamic name;
@dynamic sort;

@end
