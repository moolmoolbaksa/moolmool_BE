package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.dto.TestDto;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT DISTINCT room FROM ChatRoom room JOIN FETCH room.acceptor JOIN FETCH room.requester " +
            "WHERE room.acceptor = :user OR room.requester = :user ORDER BY room.modifiedAt DESC ")
    List<ChatRoom> findAllBy(@Param("user") User user);

    // 유저를 fetchJoin해서 찾아오면서, 동시에 방의 메시지 안읽음 카운트까지 계산하여 가져와야 합니다.
//    @Query(value =
////            "SELECT DISTINCT r.room_id AS roomId, r.modified_at AS modifiedAt, r.acc_out AS accOut, r.req_out AS reqOut " +
//            "SELECT DISTINCT r.room_id AS roomId, r.modified_at AS modifiedAt, r.acc_out AS accOut, r.req_out AS reqOut, r.acceptor_id AS acceptor, r.requester_id AS requester, " +
//                    "u1.id AS id, u1.nickname AS nickname, u1.profile AS profile, u2.id AS id, u2.nickname AS nickname, u2.profile AS profile " +
//            "FROM chat_room r " +
//            "INNER JOIN user u1 ON r.acceptor_id = u1.id " +
//            "INNER JOIN user u2 ON r.requester_id = u2.id " +
//            "WHERE r.acceptor_id = :user OR r.requester_id = :user " +
//            "ORDER BY r.modified_at DESC",
//            nativeQuery = true)
////    List<TestDto> findAllWithCnt();
//    List<TestDto> findAllWithCnt(@Param("user") User user);

    @Query("SELECT room FROM ChatRoom room JOIN FETCH room.acceptor JOIN FETCH room.requester WHERE room.id = :roomId")
    Optional<ChatRoom> findByIdFetch(Long roomId);

    // 채팅방 생성 중복검사를 해줍니다.
    Optional<ChatRoom> findByRequesterAndAcceptor(User requester, User acceptor);

    ChatRoom findByRequesterIdAndAcceptorId(Long id, Long opponentUserId);

    // 채팅방 찾아오기
    @Query("SELECT room FROM ChatRoom room " +
            "WHERE (room.requester = :requester AND room.acceptor = :acceptor) OR " +
            "(room.requester = :acceptor AND room.acceptor = :requester)")
    Optional<ChatRoom> findByUser(@Param("requester") User requester, @Param("acceptor") User acceptor);
}
