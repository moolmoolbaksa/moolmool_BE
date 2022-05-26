package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQuerydsl {
    List<Item> findAllByBagId(Long bagId);

   Optional<Item> findByTitleAndContents(String title, String contents);

}
