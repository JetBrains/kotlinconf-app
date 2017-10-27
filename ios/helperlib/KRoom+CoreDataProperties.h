//
//  KRoom+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KRoom+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KRoom (CoreDataProperties)

+ (NSFetchRequest<KRoom *> *)fetchRequest;

@property (nonatomic) int64_t id;
@property (nullable, nonatomic, copy) NSString *name;
@property (nonatomic) int64_t sort;

@end

NS_ASSUME_NONNULL_END
