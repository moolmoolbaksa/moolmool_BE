package com.sparta.mulmul.websocket.chat;

import com.sparta.mulmul.websocket.ChatFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFilterRepository extends JpaRepository<ChatFilter, Long> {
}
