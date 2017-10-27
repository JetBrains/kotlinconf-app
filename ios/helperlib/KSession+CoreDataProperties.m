//
//  KSession+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KSession+CoreDataProperties.h"

@implementation KSession (CoreDataProperties)

+ (NSFetchRequest<KSession *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KSession"];
}

@dynamic categoryItemIds;
@dynamic desc;
@dynamic endsAt;
@dynamic endsAtDate;
@dynamic id;
@dynamic roomId;
@dynamic roomName;
@dynamic speakerIds;
@dynamic startsAt;
@dynamic startsAtDate;
@dynamic subtitle;
@dynamic title;

@end
