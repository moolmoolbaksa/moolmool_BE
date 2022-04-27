package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByCategory(String category);
    List<Item> findAllByBag(Long bagId);

    // 성훈
    List<Item> findAllByBagId(Long myBagId);
}
