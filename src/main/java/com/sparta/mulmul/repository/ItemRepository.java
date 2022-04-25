package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
