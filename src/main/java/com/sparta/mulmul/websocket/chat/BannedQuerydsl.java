package com.sparta.mulmul.websocket.chat;

import com.sparta.mulmul.user.User;

import java.util.List;

public interface BannedQuerydsl {
    Boolean existsByUsers(User user, User bannedUser);
    List<User> findAllMyBannedByUser(User user);
    Boolean existsBy(Long userId, Long bannedUserId);
}
