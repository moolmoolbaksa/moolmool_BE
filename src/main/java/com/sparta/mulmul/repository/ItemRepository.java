package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemQuerydsl {
    List<Item> findAllByBagId(Long bagId);



    // 성훈 - 내 아이템 찾기 (마이페이지)
    // 물품과 보따리는 bagId, 보따리와 유저는 userId로 이어준다.
//    @Query("SELECT i FROM Item i WHERE i.bag.userId = :userId AND i.status BETWEEN  0 AND 2 ")
//    List<Item> findAllMyItem(@Param("userId") Long userId);

    @Query(value = "select * from item p where p.title like %:keyword% order by p.modified_at desc", nativeQuery = true)
    List<Item> searchByKeyword(@Param("keyword")String keyword);

//    List<Item> findAllByBagIdOrBagId(Long bagId, Long bagId1);

//    테스트를 위한 코딩
//    List<Item> findALLByBuyerIdOrSellerId(Long userId, Long userId1);

//    // 성훈 - 찜한 아이템 찾기 (마이페이지)
//    // 물품과 찜하기는 itemId, 찜하기와 유저는 userId로 이어준다.
//    @Query("SELECT i FROM Item i  INNER JOIN Scrab sc ON i.id = sc.itemId INNER JOIN User u ON sc.userId=u.id WHERE sc.userId=:userId ")
//    List<Item> findByAllMyScrabItem(@Param("userId") Long userId);

}
