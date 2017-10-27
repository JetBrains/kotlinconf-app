//
//  KAll+CoreDataClass.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class KCategory, KFavorite, KRoom, KSession, KSpeaker, KVote;

NS_ASSUME_NONNULL_BEGIN

@interface KAll : NSManagedObject

@end

NS_ASSUME_NONNULL_END

#import "KAll+CoreDataProperties.h"
