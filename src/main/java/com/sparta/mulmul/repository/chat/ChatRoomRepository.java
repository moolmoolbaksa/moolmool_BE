package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT DISTINCT room FROM ChatRoom room JOIN FETCH room.acceptor JOIN FETCH room.requester" +
            " WHERE room.acceptor = :user OR room.requester = :user ORDER BY room.modifiedAt DESC ")
    List<ChatRoom> findAllBy(@Param("user") User user);

    @Query("SELECT room FROM ChatRoom room JOIN FETCH room.acceptor JOIN FETCH room.requester WHERE room.id = :roomId")
    Optional<ChatRoom> findByIdFetch(Long roomId);

    // 채팅방 생성 중복검사를 해줍니다.
    Optional<ChatRoom> findByRequesterAndAcceptor(User requester, User acceptor);

    ChatRoom findByRequesterIdAndAcceptorId(Long id, Long opponentUserId);

}
