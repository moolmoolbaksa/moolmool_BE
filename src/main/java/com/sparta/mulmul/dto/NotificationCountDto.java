package com.sparta.mulmul.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationCountDto {

    private int NotificationCnt;

    public static NotificationCountDto valueOf(int count){

        NotificationCountDto countDto = new NotificationCountDto();
        countDto.NotificationCnt = count;
        return countDto;
    }
}
