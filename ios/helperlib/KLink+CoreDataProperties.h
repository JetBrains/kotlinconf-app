//
//  KLink+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KLink+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KLink (CoreDataProperties)

+ (NSFetchRequest<KLink *> *)fetchRequest;

@property (nullable, nonatomic, copy) NSString *linkType;
@property (nullable, nonatomic, copy) NSString *title;
@property (nullable, nonatomic, copy) NSString *url;

@end

NS_ASSUME_NONNULL_END
