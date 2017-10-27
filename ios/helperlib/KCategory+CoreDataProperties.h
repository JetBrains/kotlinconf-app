//
//  KCategory+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KCategory+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KCategory (CoreDataProperties)

+ (NSFetchRequest<KCategory *> *)fetchRequest;

@property (nonatomic) int64_t id;
@property (nullable, nonatomic, copy) NSString *name;
@property (nullable, nonatomic, retain) NSOrderedSet<KCategoryItem *> *categoryItem;

@end

@interface KCategory (CoreDataGeneratedAccessors)

- (void)insertObject:(KCategoryItem *)value inCategoryItemAtIndex:(NSUInteger)idx;
- (void)removeObjectFromCategoryItemAtIndex:(NSUInteger)idx;
- (void)insertCategoryItem:(NSArray<KCategoryItem *> *)value atIndexes:(NSIndexSet *)indexes;
- (void)removeCategoryItemAtIndexes:(NSIndexSet *)indexes;
- (void)replaceObjectInCategoryItemAtIndex:(NSUInteger)idx withObject:(KCategoryItem *)value;
- (void)replaceCategoryItemAtIndexes:(NSIndexSet *)indexes withCategoryItem:(NSArray<KCategoryItem *> *)values;
- (void)addCategoryItemObject:(KCategoryItem *)value;
- (void)removeCategoryItemObject:(KCategoryItem *)value;
- (void)addCategoryItem:(NSOrderedSet<KCategoryItem *> *)values;
- (void)removeCategoryItem:(NSOrderedSet<KCategoryItem *> *)values;

@end

NS_ASSUME_NONNULL_END
