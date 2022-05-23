package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemQuerydsl {

    Page<Item> findAllItemOrderByCreatedAtDesc(Pageable pageable);

    Page<Item> findAllItemByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
}
