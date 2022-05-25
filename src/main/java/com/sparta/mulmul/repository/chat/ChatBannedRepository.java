package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatBanned;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatBannedRepository extends JpaRepository<ChatBanned, Long>, BannedQuerydsl {

    Optional<ChatBanned> findByUserAndBannedUser(User user, User bannedUser);

}
