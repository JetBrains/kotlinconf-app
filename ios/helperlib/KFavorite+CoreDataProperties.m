//
//  KFavorite+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KFavorite+CoreDataProperties.h"

@implementation KFavorite (CoreDataProperties)

+ (NSFetchRequest<KFavorite *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KFavorite"];
}

@dynamic sessionId;

@end
