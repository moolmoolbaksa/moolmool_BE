package com.sparta.mulmul.websocket.chat;

import com.sparta.mulmul.websocket.ChatBanned;
import com.sparta.mulmul.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatBannedRepository extends JpaRepository<ChatBanned, Long>, BannedQuerydsl {

    Optional<ChatBanned> findByUserAndBannedUser(User user, User bannedUser);

}
