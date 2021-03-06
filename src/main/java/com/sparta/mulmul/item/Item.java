package com.sparta.mulmul.item;

import com.sparta.mulmul.item.itemDto.ItemUpdateRequestDto;
import com.sparta.mulmul.user.Bag;
import com.sparta.mulmul.utils.Timestamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends Timestamped {

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
    private int reportCnt;

    @Column(nullable = false)
    private int status;

    @Column(length = 1000)
    private String itemImg;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String favored;

    @Version
    private Long version;


    @ManyToOne
    private Bag bag;


    // 이승재 / 아이템 상세 페이지 접속할 때마다 조회수 올리기
    public void update(Long id, int viewCnt){
        this.id = id;
        this.viewCnt = viewCnt;
    }


    // 이승재 / 아이템 수정
    public void itemUpdate(ItemUpdateRequestDto itemUpdateRequestDto, String imgUrl, String favored) {
        this.title = itemUpdateRequestDto.getTitle();
        this.contents = itemUpdateRequestDto.getContents();
        this.category = itemUpdateRequestDto.getCategory();
        this.favored = favored;
        this.itemImg = imgUrl;
        this.type = itemUpdateRequestDto.getType();
    }

    // 이승재 / 삭제
    public void setDeleted(Long id, int status){
        this.id = id;
        this.status = status;
    }

    // 이승재 / 아이템 상태 업데이트
    public void statusUpdate(Long id, int status){
        this.id = id;
        this.status = status;
    }

    // 이승재 / 아이템 구독 정보 업데이트
    public void scrabCntUpdate(Long id, int scrabCnt){
        this.id = id;
        this.scrabCnt = scrabCnt;
    }

    //이승재 / 아이템 신고 횟수 업데이트
    public void reportCntUpdate(Long id, int reportCnt){
        this.id = id;
        this.reportCnt = reportCnt;
    }
}
