package com.sparta.mulmul.repository.chat;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.dto.chat.UnreadCntDto;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.mulmul.model.QChatMessage.*;

@Repository
public class ChatMessageRepositoryImpl implements MessageQuerydsl {

    private final JPAQueryFactory queryFactory;

    public ChatMessageRepositoryImpl(JPAQueryFactory queryFactory){
        this.queryFactory = queryFactory;
    }

    @Override
    public List<UnreadCntDto> getUnreadCnts(List<Long> roomIds, Long userId){

        // 읽지 않은 상대방의 메시지를 찾아옵니다.
        return queryFactory
                .select(
                        Projections.fields(
                                UnreadCntDto.class,
                                chatMessage.roomId,
                                chatMessage.count().as("unreadCnt")
                        ))
                .from(chatMessage)
                .where(
                        chatMessage.roomId.in(roomIds),
                        chatMessage.isRead.eq(false),
                        chatMessage.senderId.ne(userId)
                )
                .groupBy(chatMessage.roomId)
                .fetch();
    }
}
