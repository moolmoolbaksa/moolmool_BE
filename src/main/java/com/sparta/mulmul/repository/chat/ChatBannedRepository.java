package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.model.ChatBanned;
import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatBannedRepository extends JpaRepository<ChatBanned, Long> {

    // QueryDSL 을 도입하여, existsBy로 최적화를 시켜줘야 합니다. 혹은 mySql의 네이티브 쿼리를 작성해 줍시다.
    @Query("SELECT COUNT(c.id) > 0 FROM ChatBanned c " +
            "WHERE (c.user = :user) AND (c.bannedUser = :bannedUser) OR (c.user = :bannedUser AND c.bannedUser = :user)")
    boolean existsByUser(@Param("user") User user, @Param("bannedUser") User bannedUser);

}
