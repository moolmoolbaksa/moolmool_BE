package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CompleteBarterDto {
    private Long barterId;
    private boolean isGraded;
    private LocalDateTime date;
    private List<MyBarterDto> myItem;
    private List<MyBarterDto> barterItem;


    public CompleteBarterDto(Long barterId, boolean isGraded, LocalDateTime date, List<MyBarterDto> myItem, List<MyBarterDto> barterItem) {
        this.barterId = barterId;
        this.isGraded = isGraded;
        this.date = date;
        this.myItem = myItem;
        this.barterItem = barterItem;
    }
}
