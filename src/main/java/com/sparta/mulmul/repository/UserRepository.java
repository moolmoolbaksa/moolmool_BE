package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
