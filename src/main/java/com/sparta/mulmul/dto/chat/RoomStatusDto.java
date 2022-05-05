package com.sparta.mulmul.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomStatusDto {

    private MessageTypeEnum type;

    public static RoomStatusDto valueOf(MessageTypeEnum type){

        RoomStatusDto statusDto = new RoomStatusDto();
        statusDto.type = type;

        return statusDto;
    }

}
