package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int itemCnt;

    @Column(nullable = false)
    private Long userId;


    // 보따리에 아이템이 생성될때마다 카운트 1 늘리기기
    public void update(int itemCnt){
        this.itemCnt = itemCnt;
    }

    public Bag(User user){
        this.userId = user.getId();
        this.itemCnt = 0;
    }
}
