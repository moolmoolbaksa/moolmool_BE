package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


// 성훈 - 거래내역
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Barter extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "barter_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private Long sellerId;

    // 일단은 보류 -> ManyToOne 같은 객체참조를 쓸지는 미정
    @Column(nullable = false)
    private String barter;

    // 거래내역상태 0 : 거래중 / 1 : 거래완료 / 2 : 평가 완료
    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private Boolean isBuyerTrade;

    @Column(nullable = false)
    private Boolean isSellerTrade;

    @Column(nullable = false)
    private Boolean isBuyerScore;

    @Column(nullable = false)
    private Boolean isSellerScore;


    private LocalDateTime tradeTime;


    public void updateBarter(int status) {
        this.status = status;
    }

    public void editBarter(String barter) {
        this.barter = barter;
    }

    public void updateTradeBarter(int status, LocalDateTime tradeTime) {
        this.status = status;
        this.tradeTime = tradeTime;
    }

    public void updateTradBuyer(Boolean isBuyerTrade) {

        this.isBuyerTrade = isBuyerTrade;
    }
    public void updateTradSeller(Boolean isSellerTrade) {

        this.isSellerTrade = isSellerTrade;
    }

    public void updateScoreBuyer(Boolean isBuyerScore) {

        this.isBuyerScore = isBuyerScore;
    }

    public void updateScoreSeller(Boolean isSellerScore) {

        this.isSellerScore = isSellerScore;
    }
}


