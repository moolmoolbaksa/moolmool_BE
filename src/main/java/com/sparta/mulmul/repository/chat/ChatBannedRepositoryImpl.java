package com.sparta.mulmul.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.model.User;

import java.util.List;

import static com.sparta.mulmul.model.QChatBanned.*;

public class ChatBannedRepositoryImpl implements BannedQuerydsl {

    private final JPAQueryFactory queryFactory;

    public ChatBannedRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsByUsers(User user, User bannedUser){
        Integer fetchOne = queryFactory
                .selectOne()
                .from(chatBanned)
                .where(chatBanned.user.id.eq(user.getId()).or(chatBanned.bannedUser.id.eq(bannedUser.getId())),
                        chatBanned.user.id.eq(bannedUser.getId()).or(chatBanned.bannedUser.id.eq(user.getId())),
                        chatBanned.isBanned.eq(true))
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public List<User> findAllMyBannedByUser(User user){
        return queryFactory
                .select(chatBanned.bannedUser)
                .from(chatBanned)
                .where(chatBanned.user.eq(user), chatBanned.isBanned.eq(true))
                .fetch();
    }

    @Override
    public Boolean existsBy(Long userId, Long bannedUserId){
        Integer fetchOne = queryFactory
                .selectOne()
                .from(chatBanned)
                .where(
                        chatBanned.user.id.eq(userId).and(chatBanned.bannedUser.id.eq(bannedUserId))
                                .or(
                                        chatBanned.user.id.eq(bannedUserId).and(chatBanned.bannedUser.id.eq(userId))
                                ).and(chatBanned.isBanned.isTrue())
                )
                .fetchFirst();
        return fetchOne != null;
    }

}
