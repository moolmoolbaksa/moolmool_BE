package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.User;

import java.util.List;

public interface BannedQuerydsl {
    Boolean existsByUsers(User user, User bannedUser);
    List<User> findAllMyBannedByUser(User user);
    Boolean existsBy(Long userId, Long bannedUserId);
}
