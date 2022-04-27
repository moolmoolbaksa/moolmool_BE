package com.sparta.mulmul.repository;



import com.sparta.mulmul.model.Scrab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ScrabRepository extends JpaRepository<Scrab, Long> {
//    @Query(value = "select * from scrab p where p.userId = ?1 and p.itemId = ?2")
    Optional<Scrab> findByUserIdAndItemId(Long userId, Long itemId);
}