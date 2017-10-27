//
//  KCategoryItem+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KCategoryItem+CoreDataProperties.h"

@implementation KCategoryItem (CoreDataProperties)

+ (NSFetchRequest<KCategoryItem *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KCategoryItem"];
}

@dynamic id;
@dynamic name;
@dynamic category;

@end
