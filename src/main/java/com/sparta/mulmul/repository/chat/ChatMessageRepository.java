package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByRoomIdOrderByIdDesc(Long roomId);

    Slice<ChatMessage> findAllByRoomIdOrderByIdDesc(Long roomId, Pageable page);

    // 그룹에서 가장 최근의 메시지를 찾아오는 쿼리문을 작성해 보도록 합니다.
    Optional<ChatMessage> findFirstByRoomIdOrderByIdDesc(Long roomID);

    // 내가 요청받은 채팅방
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.senderId =:userId AND cm.roomId =:roomId")
    List<ChatMessage> findMyChatMessage(@Param("userId") Long userId, @Param("roomId") Long roomId);

    // 내가 요청받은 채팅방
    Optional<ChatMessage> findFirstBySenderIdAndRoomId(Long userId, Long roomID);

    // 메시지 안읽은 갯수 카운트 쿼리
    @Query("SELECT count(msg) FROM ChatMessage msg WHERE msg.senderId =:userId AND msg.roomId =:roomId AND msg.isRead = false")
    int countMsg(Long userId, Long roomId);

    // 채팅 메시지 읽음 상태 일괄 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage msg SET msg.isRead = true WHERE msg.roomId = :roomId AND msg.senderId <> :userId AND msg.isRead = false")
    void updateChatMessage(Long roomId, Long userId);

    // mySQL로 네이티브 쿼리를 작성해 줘야 할 것 같습니다.
//    @Query("SELECT msg FROM ChatMessage msg WHERE msg.roomId IN :roomIds")
    @Query(value = "SELECT * FROM chat_message msg WHERE msg.room_id IN (SELECT msg.room_id, MAX(msg.room_id) FROM chat_message msg)", nativeQuery = true)
    List<ChatMessage> findFirstByRoomIds(@Param("roomIds") List<Long> roomIds);
}
