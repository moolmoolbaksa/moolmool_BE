package com.sparta.mulmul.repository;


import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BarterRepository extends JpaRepository<Barter, Long> {

    @Query("SELECT b FROM Barter b WHERE b.buyerId = : userId ")
    List<Barter> findAllMybarter(@Param("userId") Long userId);
}
