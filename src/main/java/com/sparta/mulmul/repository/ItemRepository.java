package com.sparta.mulmul.repository;

import com.sparta.mulmul.model.Item;
import org.hibernate.annotations.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.TypedQuery;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByCategory(String category);

    List<Item> findAllByBagId(Long bagId);

    List<Item> findAllByOrderByCreatedAtDesc();

    // 성훈 - 내 아이템 찾기 (마이페이지)
    // 물품과 보따리는 bagId, 보따리와 유저는 userId로 이어준다.
    @Query("SELECT i FROM Item i WHERE i.bag.userId = :userId")
    List<Item> findAllMyItem(@Param("userId") Long userId);

//    List<Item> findAllByBagIdOrBagId(Long bagId, Long bagId1);

//    테스트를 위한 코딩
//    List<Item> findALLByBuyerIdOrSellerId(Long userId, Long userId1);

//    // 성훈 - 찜한 아이템 찾기 (마이페이지)
//    // 물품과 찜하기는 itemId, 찜하기와 유저는 userId로 이어준다.
//    @Query("SELECT i FROM Item i  INNER JOIN Scrab sc ON i.id = sc.itemId INNER JOIN User u ON sc.userId=u.id WHERE sc.userId=:userId ")
//    List<Item> findByAllMyScrabItem(@Param("userId") Long userId);

}
