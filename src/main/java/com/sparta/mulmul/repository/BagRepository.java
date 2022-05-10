package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Bag;
import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BagRepository extends JpaRepository<Bag, Long> {
    Bag findByUserId(Long userId);
}
