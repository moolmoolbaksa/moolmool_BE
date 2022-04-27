package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


// 성훈 - 거래내역
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Barter{
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "barter_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private Long sellerId;

    // 일단은 보류 -> ManyToOne 같은 객체참조를 쓸지는 미정
    @Column(nullable = false)
    private String barter;


}