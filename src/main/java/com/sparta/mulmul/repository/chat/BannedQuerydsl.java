package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.User;

public interface BannedQuerydsl {
    Boolean existsByUser(User user, User bannedUser);
}
