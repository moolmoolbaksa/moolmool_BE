package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatBanned;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatBannedRepository extends JpaRepository<ChatBanned, Long>, BannedQuerydsl {
}
