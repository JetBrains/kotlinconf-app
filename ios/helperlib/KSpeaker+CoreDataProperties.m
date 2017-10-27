//
//  KSpeaker+CoreDataProperties.m
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KSpeaker+CoreDataProperties.h"

@implementation KSpeaker (CoreDataProperties)

+ (NSFetchRequest<KSpeaker *> *)fetchRequest {
	return [[NSFetchRequest alloc] initWithEntityName:@"KSpeaker"];
}

@dynamic bio;
@dynamic firstName;
@dynamic fullName;
@dynamic id;
@dynamic isTopSpeaker;
@dynamic lastName;
@dynamic profilePicture;
@dynamic tagLine;
@dynamic link;

@end
