//
//  KCategoryItem+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KCategoryItem+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KCategoryItem (CoreDataProperties)

+ (NSFetchRequest<KCategoryItem *> *)fetchRequest;

@property (nonatomic) int64_t id;
@property (nullable, nonatomic, copy) NSString *name;
@property (nullable, nonatomic, retain) KCategory *category;

@end

NS_ASSUME_NONNULL_END
