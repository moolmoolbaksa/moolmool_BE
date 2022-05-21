package com.sparta.mulmul.repository.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.model.User;

import static com.sparta.mulmul.model.QChatBanned.*;

public class ChatBannedRepositoryImpl implements BannedQuerydsl {

    private final JPAQueryFactory queryFactory;

    public ChatBannedRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsByUser(User user, User bannedUser){
        Integer fetchOne = queryFactory
                .selectOne()
                .from(chatBanned)
                .where(chatBanned.user.id.eq(user.getId()).or(chatBanned.bannedUser.id.eq(bannedUser.getId())),
                        chatBanned.user.id.eq(bannedUser.getId()).or(chatBanned.bannedUser.id.eq(user.getId()))
                ).fetchFirst();
        return fetchOne != null;
    };

}
