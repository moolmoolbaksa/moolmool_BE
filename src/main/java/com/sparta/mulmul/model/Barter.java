package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


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
    private Boolean isBuyerTrad = false;

    @Column(nullable = false)
    private Boolean isSellerTrad = false;

    @Column(nullable = false)
    private Boolean isBuyerScore = false;

    @Column(nullable = false)
    private Boolean isSellerScore = false;


    public void updateBarter(int status) {
        this.status = status;
    }


    public void updateTradBuyer(Boolean isBuyerTrad) {

        this.isBuyerTrad = isBuyerTrad;
    }
    public void updateTradSeller(Boolean isSellerTrad) {

        this.isSellerTrad = isSellerTrad;
    }

    public void updateScoreBuyer(Boolean isBuyerScore) {

        this.isBuyerScore = isBuyerScore;
    }

    public void updateScoreSeller(Boolean isSellerScore) {

        this.isSellerScore = isSellerScore;
    }
}


