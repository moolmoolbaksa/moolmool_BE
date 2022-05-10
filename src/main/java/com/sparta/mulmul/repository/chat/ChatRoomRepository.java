package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // fetch-join으로 리펙토링해야 합니다. 채팅메시지와 연관관계를 맺어, modifiedAt을 이용해 최신을 정렬해 주도록 합니다.
    List<ChatRoom> findAllByAcceptorOrRequesterOrderByModifiedAtDesc(User acceptor, User requester);

    // 채팅방 생성 중복검사를 해줍니다.
    Optional<ChatRoom> findByRequesterAndAcceptor(User requester, User acceptor);

    ChatRoom findByRequesterIdAndAcceptorId(Long id, Long opponentUserId);


    // 성훈 - 내 아이템 찾기 (마이페이지)
//    // 내가 요청한 채팅방
//    @Query("SELECT cr FROM ChatRoom cr WHERE cr.requester =:user AND  cr.acceptor = :opponentUser")
//    ChatRoom findMyRequestChatRoom(@Param("user") User user, @Param("opponentUser") User opponentUser);
//
//    // 내가 요청받은 채팅방
//    @Query("SELECT cr FROM ChatRoom cr WHERE cr.requester = :opponentUser AND cr.acceptor = :user")
//    ChatRoom findMyAcceptorIdChatRoom(@Param("user") User user, @Param("opponentUser") User opponentUser);


}
