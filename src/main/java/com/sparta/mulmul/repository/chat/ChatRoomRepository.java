package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // fetch-join으로 리펙토링해야 합니다. 채팅메시지와 연관관계를 맺어, modifiedAt을 이용해 최신을 정렬해 주도록 합니다.
    List<ChatRoom> findAllByAcceptorOrRequesterOrderByModifiedAtDesc(User acceptor, User requester);

    // 채팅방 생성 중복검사를 해줍니다.
    Optional<ChatRoom> findByRequesterAndAcceptor(User requester, User acceptor);

}
