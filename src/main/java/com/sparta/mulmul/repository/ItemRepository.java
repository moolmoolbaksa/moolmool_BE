package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQuerydsl {
    List<Item> findAllByCategory(String category);

    List<Item> findAllByBagId(Long bagId);


    @Query(value = "select * from item p where p.title like %:keyword% order by p.modified_at desc", nativeQuery = true)
    List<Item> searchByKeyword(@Param("keyword")String keyword);


}
