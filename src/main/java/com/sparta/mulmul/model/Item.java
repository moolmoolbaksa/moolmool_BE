package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.ItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends Timestamped{

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
    private int status;

    @Column(length = 1000)
    private String itemImg;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String favored;

    @ManyToOne
    private Bag bag;


    // 이승재 / 아이템 상세 페이지 접속할 때마다 조회수 올리기
    public void update(int viewCnt){
        this.viewCnt = viewCnt;
    }


    // 이승재 / 아이템 수정
    public void itemUpdate(ItemRequestDto itemRequestDto, String imgUrl, String favored) {
        this.title = itemRequestDto.getTitle();
        this.contents = itemRequestDto.getContents();
        this.category = itemRequestDto.getCategory();
        this.favored = imgUrl;
        this.itemImg = favored;
        this.type = itemRequestDto.getType();
    }

    // 이승재 / 아이템 상태 업데이트
    public void statusUpdate(int Status){
        this.status = status;
    }
}
