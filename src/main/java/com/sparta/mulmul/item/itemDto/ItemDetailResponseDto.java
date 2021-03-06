package com.sparta.mulmul.item.itemDto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ItemDetailResponseDto {
    private Long userId;
    private Long itemId;
    private String nickname;
    private String degree;
    private float grade;
    private String profile;
    private int status;
    private String category;
    private List<String> images;
    private String  title;
    private String contents;
    private String address;
    private LocalDateTime date;
    private int viewCnt;
    private int scrabCnt;
    private String type;
    private String[] favored;
    private boolean isScrab;
    private String traded;
    private Long barterId;


    // 이승재 / 아이템 상세페이지
    public ItemDetailResponseDto(Long i,
                           Long itemId,
                           String nickname,
                           String degree,
                           float v,
                           String profile,
                           int status,
                           String category,
                           List<String> itemImgList,
                           String title,
                           String contents,
                           String address,
                           LocalDateTime createdAt,
                           int viewCnt,
                           int scrabCnt,
                           String type,
                           String[] favored,
                           boolean isScrab,
                                 String traded,
                                 Long barterId) {
        this.userId = i;
        this.itemId = itemId;
        this.nickname = nickname;
        this.degree = degree;
        this.grade = v;
        this.profile = profile;
        this.status = status;
        this.category = category;
        this.images = itemImgList;
        this.title = title;
        this.contents = contents;
        this.address =address;
        this.date = createdAt;
        this.viewCnt = viewCnt;
        this.scrabCnt = scrabCnt;
        this.type = type;
        this.favored = favored;
        this.isScrab = isScrab;
        this.traded = traded;
        this.barterId = barterId;
    }
}
