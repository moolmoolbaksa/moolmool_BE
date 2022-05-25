package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatBanned;
import com.sparta.mulmul.model.User;

import java.util.List;
import java.util.Optional;

public interface BannedQuerydsl {
    Boolean existsByUsers(User user, User bannedUser);
    List<User> findAllMyBannedByUser(User user);
    Boolean existsByUser(Long userId, Long bannedUserId);
    Boolean existsBy(Long userId, Long bannedUserId);
}
