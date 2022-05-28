package com.sparta.mulmul.websocket;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.mulmul.websocket.QNotification.notification;
import static com.sparta.mulmul.websocket.chatDto.NotificationType.FINISH;

@Repository
public class NotificationRepositoryImpl implements NotificationQuerydsl {

    private final JPAQueryFactory queryFactory;

    public NotificationRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    @Override
    public List<Notification> findAllByUserIdAndChangeId(Long userId, Long changeId) {
        return queryFactory
                .selectFrom(notification)
                .where(notification.userId.eq(userId),
                        notification.changeId.eq(changeId),
                        notification.type.eq(FINISH))
                .fetch();
    }
}
