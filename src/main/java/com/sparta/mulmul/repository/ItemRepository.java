package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import jdk.nashorn.internal.ir.Optimistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQuerydsl {
    List<Item> findAllByBagId(Long bagId);

   Optional<Item> existsByItemImg(String imgUrl);
}
