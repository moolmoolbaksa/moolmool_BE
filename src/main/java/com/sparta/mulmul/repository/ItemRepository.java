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
    //검색어를 받아서 최신순으로 정렬한다.
//    @Query("select i from Item i where i.title like %:keyword% order by i.id desc")
//    List<Item> searchByKeyword(String keyword);
//
//    List<Item> findByTitleContaining(String keyword);
}
