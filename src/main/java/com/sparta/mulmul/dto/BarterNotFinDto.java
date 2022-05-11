package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BarterNotFinDto {
    private Long barterId;
    private Long userId;
    private String usernickname;
    private String profile;
    private int status;
    private String myPosition;
    private Boolean isTrade;
    private List<MyBarterDto> myItem;
    private List<MyBarterDto> barterItem;


    public BarterNotFinDto(Long barterId, Long userId, String usernickname, String profile, int status, String myPosition, Boolean isTrade,  List<MyBarterDto> myItem, List<MyBarterDto> barterItem) {
        this.barterId = barterId;
        this.userId = userId;
        this.usernickname = usernickname;
        this.profile = profile;
        this.status = status;
        this.myPosition = myPosition;
        this.isTrade = isTrade;
        this.myItem = myItem;
        this.barterItem = barterItem;
    }
}
