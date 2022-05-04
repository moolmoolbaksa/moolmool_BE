package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BarterNotFinDto {
    private Long barterId;
    private Long opponentId;
    private String opponentnickname;
    private String opponentProfile;
    private int status;
    private String myPosition;
    private List<MyBarterDto> myItem;
    private List<MyBarterDto> barterItem;


    public BarterNotFinDto(Long barterId, Long opponentId, String opponentnickname, String opponentProfile, int status, String myPosition,  List<MyBarterDto> myItem, List<MyBarterDto> barterItem) {
        this.barterId = barterId;
        this.opponentId = opponentId;
        this.opponentnickname = opponentnickname;
        this.opponentProfile = opponentProfile;
        this.status = status;
        this.myPosition = myPosition;
        this.myItem = myItem;
        this.barterItem = barterItem;
    }
}
