package com.sparta.mulmul.repository;


import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByUserId(Long userId);

    List<Item> findAllByUserIdAndItemId(Long buyerId, String[] buyerItemIdList);
}
