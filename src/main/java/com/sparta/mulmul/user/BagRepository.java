package com.sparta.mulmul.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BagRepository extends JpaRepository<Bag, Long> {
    Bag findByUserId(Long userId);
}
