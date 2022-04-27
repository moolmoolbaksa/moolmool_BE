package com.sparta.mulmul.repository;


import com.sparta.mulmul.model.Barter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarterRepository extends JpaRepository<Barter, Long> {

    List<Barter> findAllByBuyerIdOrSellerId(Long userId);
}
