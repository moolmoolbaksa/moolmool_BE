package com.sparta.mulmul.repository;


import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BarterRepository extends JpaRepository<Barter, Long> {

    List<Barter> findAllByBuyerIdOrSellerId(Long userId, Long userId1);
}
