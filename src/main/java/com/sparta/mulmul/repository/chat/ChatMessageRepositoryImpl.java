package com.sparta.mulmul.repository.chat;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.mulmul.dto.chat.UnreadCntDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.mulmul.model.QChatMessage.*;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements MessageQuerydsl {


    private final JPAQueryFactory queryFactory;

    @Override
    public List<UnreadCntDto> getUnreadCnts(List<Long> roomIds, Long userId){

        if ( roomIds == null ) { return new ArrayList<>(); } // 읽지 않은 상대방의 메시지를 찾아옵니다. null 일 경우 예외 처리

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
