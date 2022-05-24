package com.sparta.mulmul.repository;


import com.sparta.mulmul.model.Barter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BarterRepository extends JpaRepository<Barter, Long> , BarterQuerydsl {

    List<Barter> findAllByBuyerIdOrSellerId(Long userId, Long userId1);

    @Query("SELECT b FROM Barter b WHERE b.buyerId = :userId OR b.sellerId = :userId")
    List<Barter> findAllByUserId(@Param("userId") Long userId);

    @Query("select b from Barter b where b.status = :status order by b.sellerId desc")
    List<Barter> findAllByBarter(@Param("status") int status);

    Optional<Barter> findByBarter(String stringBarter);
    List<Barter> findAllByBuyerIdAndSellerId(Long buyerId, Long sellerId);

    List<Barter> findAllBySellerId(Long sellerId);
}
