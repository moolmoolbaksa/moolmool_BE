package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByRequesterIdAndAcceptorId(Long id, Long opponentUserId);

    // 성훈 - 내 아이템 찾기 (마이페이지)
    // 내가 요청한 채팅방
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.requesterId =:userId AND  cr.acceptorId = :opponentUserId")
    ChatRoom findMyRequestChatRoom(@Param("userId") Long userId, @Param("opponentUserId") Long opponentUserId);


    // 내가 요청받은 채팅방
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.requesterId = :opponentUserId AND cr.acceptorId = :userId")
    ChatRoom findMyAcceptorIdChatRoom(@Param("userId") Long userId, @Param("opponentUserId") Long opponentUserId);

}
