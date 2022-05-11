package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomIdOrderByIdDesc(Long roomId);

    // 그룹에서 가장 최근의 메시지를 찾아오는 쿼리문을 작성해 보도록 합니다.
    Optional<ChatMessage> findFirstByRoomIdOrderByIdDesc(Long roomID);
}
