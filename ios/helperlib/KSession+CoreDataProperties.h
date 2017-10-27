//
//  KSession+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KSession+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KSession (CoreDataProperties)

+ (NSFetchRequest<KSession *> *)fetchRequest;

@property (nullable, nonatomic, retain) NSArray *categoryItemIds;
@property (nullable, nonatomic, copy) NSString *desc;
@property (nullable, nonatomic, copy) NSString *endsAt;
@property (nullable, nonatomic, copy) NSDate *endsAtDate;
@property (nullable, nonatomic, copy) NSString *id;
@property (nonatomic) int64_t roomId;
@property (nullable, nonatomic, copy) NSString *roomName;
@property (nullable, nonatomic, retain) NSArray *speakerIds;
@property (nullable, nonatomic, copy) NSString *startsAt;
@property (nullable, nonatomic, copy) NSDate *startsAtDate;
@property (nullable, nonatomic, copy) NSString *subtitle;
@property (nullable, nonatomic, copy) NSString *title;

@end

NS_ASSUME_NONNULL_END
