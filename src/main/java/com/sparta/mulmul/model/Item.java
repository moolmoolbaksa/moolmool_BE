package com.sparta.mulmul.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.mapping.Bag;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

//    @ManyToOne
//    private Bag bag;

}
