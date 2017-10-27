//
//  KFavorite+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KFavorite+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KFavorite (CoreDataProperties)

+ (NSFetchRequest<KFavorite *> *)fetchRequest;

@property (nullable, nonatomic, copy) NSString *sessionId;

@end

NS_ASSUME_NONNULL_END
