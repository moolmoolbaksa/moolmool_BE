package com.sparta.mulmul.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQuerydsl {
    List<Item> findAllByBagId(Long bagId);

   Optional<Item> findByTitleAndContents(String title, String contents);


    @Query("SELECT i FROM Item i WHERE i.id IN :itemIds")
    List<Item> findAllByItemIds(@Param("itemIds") Long[] itemIds);

}
