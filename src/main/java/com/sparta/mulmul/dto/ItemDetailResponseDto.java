package com.sparta.mulmul.dto;


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
    private String nickname;
    private String degree;
    private float grade;
    private String profile;
    private int status;
    private List<String> images;
    private List<String> bagImages;
    private String  title;
    private String contents;
    private LocalDateTime date;
    private int viewCnt;
    private int scrabCnt;
    private Boolean isScrab;


    // 이승재 / 아이템 상세페이지
    public ItemDetailResponseDto(Long i,
                           String nickname,
                           String degree,
                           float v,
                           String profile,
                           int status,
                           List<String> itemImgList,
                           List<String> bagImages,
                           String title,
                           String contents,
                           LocalDateTime createdAt,
                           int viewCnt,
                           int scrabCnt,
                           Boolean isScrab) {
        this.userId = i;
        this.nickname = nickname;
        this.degree = degree;
        this.grade = v;
        this.profile = profile;
        this.status = status;
        this.images = itemImgList;
        this.bagImages = bagImages;
        this.title = title;
        this.contents = contents;
        this.date = createdAt;
        this.viewCnt = viewCnt;
        this.scrabCnt = scrabCnt;
        this.isScrab = isScrab;
    }
}
