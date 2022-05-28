package com.sparta.mulmul.item;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ScrabRepository extends JpaRepository<Scrab, Long>, ScrabQuerydsl {
//    @Query(value = "select * from scrab p where p.userId = ?1 and p.itemId = ?2")
    Optional<Scrab> findByUserIdAndItemId(Long userId, Long itemId);

//    List<Scrab> findAllByUserId(Long userId);

    List<Scrab> findAllByItemId(Long itemId);


    List<Scrab> findAllByUserIdOrderByModifiedAtDesc(Long userId);
}