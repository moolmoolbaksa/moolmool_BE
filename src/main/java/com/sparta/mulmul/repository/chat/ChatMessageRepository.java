package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    ChatMessage findByUserId(Long oppentUserId);

    List<ChatMessage> findAllByRoomIdAndUserId(Long roomId, Long userId);

    // 내가 요청받은 채팅방
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.userId =:userId AND cm.roomId =:roomId")
    List<ChatMessage> findMyChatMessage(@Param("userId") Long userId, @Param("roomId") Long roomId);
}
