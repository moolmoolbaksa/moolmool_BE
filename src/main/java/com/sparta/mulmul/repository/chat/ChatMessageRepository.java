package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
