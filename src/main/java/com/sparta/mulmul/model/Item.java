package com.sparta.mulmul.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Item {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "bag_id", nullable = false)
    private Bag bag;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int scrabCnt;

    @Column(nullable = false)
    private int commentCnt;

    @Column(nullable = false)
    private int viewCnt;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String itemImg;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String favored;

}
