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

   Optional<Item> findByTitleAndContents(String title, String contents);

    @Query(value = "select * from item p where p.title like %:keyword% order by p.modified_at desc", nativeQuery = true)
    List<Item> searchByKeyword(@Param("keyword")String keyword);


   Optional<Item> existsByItemImg(String imgUrl);
}
