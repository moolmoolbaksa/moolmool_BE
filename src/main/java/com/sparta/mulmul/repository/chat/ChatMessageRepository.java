package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByRoomIdOrderByIdDesc(Long roomId);

    // 그룹에서 가장 최근의 메시지를 찾아오는 쿼리문을 작성해 보도록 합니다.
    Optional<ChatMessage> findFirstByRoomIdOrderByIdDesc(Long roomID);

    // 내가 요청받은 채팅방
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.senderId =:userId AND cm.roomId =:roomId")
    List<ChatMessage> findMyChatMessage(@Param("userId") Long userId, @Param("roomId") Long roomId);

    // 내가 요청받은 채팅방
    Optional<ChatMessage> findFirstBySenderIdAndRoomId(Long userId, Long roomID);
}
