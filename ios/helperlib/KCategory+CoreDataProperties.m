//
//  KCategory+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KCategory+CoreDataProperties.h"

@implementation KCategory (CoreDataProperties)

+ (NSFetchRequest<KCategory *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KCategory"];
}

@dynamic id;
@dynamic name;
@dynamic categoryItem;

@end
