package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Bag {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "Bag_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private int itemCnt;

    @Column(name = "user_id", nullable = false)
    private Long userId;


}
