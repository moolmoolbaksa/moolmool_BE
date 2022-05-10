package com.sparta.mulmul.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class BarterFinDto {
    private Long barterId;
    private Long opponentId;
    private String opponentnickname;
    private String opponentProfile;
    private LocalDateTime date;
    private int status;
    private String myPosition;
    private Boolean isScore;
    private List<MyBarterDto> myItem;
    private List<MyBarterDto> barterItem;



    public BarterFinDto(Long barterId, Long opponentId, String opponentnickname, String opponentProfile, LocalDateTime date, int status, String myPosition, Boolean isScore, List<MyBarterDto> myItem, List<MyBarterDto> barterItem) {
        this.barterId = barterId;
        this.opponentId = opponentId;
        this.opponentnickname = opponentnickname;
        this.opponentProfile = opponentProfile;
        this.date = date;
        this.status = status;
        this.myPosition = myPosition;
        this.isScore = isScore;
        this.myItem = myItem;
        this.barterItem = barterItem;
    }
}
