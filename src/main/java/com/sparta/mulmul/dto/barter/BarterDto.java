package com.sparta.mulmul.dto.barter;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class BarterDto {
    private Long barterId;
    private Long userId;
    private String usernickname;
    private String profile;
    private LocalDateTime date;
    private int status;
    private String myPosition;
    private Boolean isScore;
    private Boolean isTrade;
    private List<OpponentBarterDto> myItem;
    private List<OpponentBarterDto> barterItem;



    public BarterDto(Long barterId, Long userId, String usernickname, String profile, LocalDateTime date, int status, String myPosition , Boolean isTrade, Boolean isScore, List<OpponentBarterDto> myItem, List<OpponentBarterDto> barterItem) {
        this.barterId = barterId;
        this.userId = userId;
        this.usernickname = usernickname;
        this.profile = profile;
        this.date = date;
        this.status = status;
        this.myPosition = myPosition;
        this.isTrade = isTrade;
        this.isScore = isScore;
        this.myItem = myItem;
        this.barterItem = barterItem;
    }
}
