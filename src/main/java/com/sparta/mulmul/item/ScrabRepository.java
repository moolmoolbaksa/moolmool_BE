package com.sparta.mulmul.item;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ScrabRepository extends JpaRepository<Scrab, Long>, ScrabQuerydsl {

    Optional<Scrab> findByUserIdAndItemId(Long userId, Long itemId);



    List<Scrab> findAllByItemId(Long itemId);


    List<Scrab> findAllByUserIdOrderByModifiedAtDesc(Long userId);
}
