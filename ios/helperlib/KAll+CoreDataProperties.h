//
//  KAll+CoreDataProperties.h
//  helperlib
//
//  Created by Yan Zhulanow on 30/10/2017.
//  Copyright © 2017 JetBrains. All rights reserved.
//
//

#import "KAll+CoreDataClass.h"


NS_ASSUME_NONNULL_BEGIN

@interface KAll (CoreDataProperties)

+ (NSFetchRequest<KAll *> *)fetchRequest;

@property (nullable, nonatomic, retain) NSSet<KCategory *> *category;
@property (nullable, nonatomic, retain) NSSet<KRoom *> *room;
@property (nullable, nonatomic, retain) NSSet<KSession *> *session;
@property (nullable, nonatomic, retain) NSSet<KSpeaker *> *speaker;
@property (nullable, nonatomic, retain) NSSet<KVote *> *vote;
@property (nullable, nonatomic, retain) NSSet<KFavorite *> *favorites;

@end

@interface KAll (CoreDataGeneratedAccessors)

- (void)addCategoryObject:(KCategory *)value;
- (void)removeCategoryObject:(KCategory *)value;
- (void)addCategory:(NSSet<KCategory *> *)values;
- (void)removeCategory:(NSSet<KCategory *> *)values;

- (void)addRoomObject:(KRoom *)value;
- (void)removeRoomObject:(KRoom *)value;
- (void)addRoom:(NSSet<KRoom *> *)values;
- (void)removeRoom:(NSSet<KRoom *> *)values;

- (void)addSessionObject:(KSession *)value;
- (void)removeSessionObject:(KSession *)value;
- (void)addSession:(NSSet<KSession *> *)values;
- (void)removeSession:(NSSet<KSession *> *)values;

- (void)addSpeakerObject:(KSpeaker *)value;
- (void)removeSpeakerObject:(KSpeaker *)value;
- (void)addSpeaker:(NSSet<KSpeaker *> *)values;
- (void)removeSpeaker:(NSSet<KSpeaker *> *)values;

- (void)addVoteObject:(KVote *)value;
- (void)removeVoteObject:(KVote *)value;
- (void)addVote:(NSSet<KVote *> *)values;
- (void)removeVote:(NSSet<KVote *> *)values;

- (void)addFavoritesObject:(KFavorite *)value;
- (void)removeFavoritesObject:(KFavorite *)value;
- (void)addFavorites:(NSSet<KFavorite *> *)values;
- (void)removeFavorites:(NSSet<KFavorite *> *)values;

@end

NS_ASSUME_NONNULL_END
