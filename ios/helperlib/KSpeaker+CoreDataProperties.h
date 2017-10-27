//
//  KSpeaker+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KSpeaker+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KSpeaker (CoreDataProperties)

+ (NSFetchRequest<KSpeaker *> *)fetchRequest;

@property (nullable, nonatomic, copy) NSString *bio;
@property (nullable, nonatomic, copy) NSString *firstName;
@property (nullable, nonatomic, copy) NSString *fullName;
@property (nullable, nonatomic, copy) NSString *id;
@property (nonatomic) BOOL isTopSpeaker;
@property (nullable, nonatomic, copy) NSString *lastName;
@property (nullable, nonatomic, copy) NSString *profilePicture;
@property (nullable, nonatomic, copy) NSString *tagLine;
@property (nullable, nonatomic, retain) NSSet<KLink *> *link;

@end

@interface KSpeaker (CoreDataGeneratedAccessors)

- (void)addLinkObject:(KLink *)value;
- (void)removeLinkObject:(KLink *)value;
- (void)addLink:(NSSet<KLink *> *)values;
- (void)removeLink:(NSSet<KLink *> *)values;

@end

NS_ASSUME_NONNULL_END
