package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Bag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BagRepository extends JpaRepository<Bag, Long> {
    Bag findByUserId(Long userId);
}
