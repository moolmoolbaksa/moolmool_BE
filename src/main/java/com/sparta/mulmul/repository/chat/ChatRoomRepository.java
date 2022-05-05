package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}