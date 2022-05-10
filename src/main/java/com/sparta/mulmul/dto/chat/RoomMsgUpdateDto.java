package com.sparta.mulmul.dto.chat;

import com.sparta.mulmul.websocket.TempChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomMsgUpdateDto {

    private String roomId;
    private String message;
    private LocalDateTime date;

    public static RoomMsgUpdateDto createFrom(TempChatRoom tempRoom){
        RoomMsgUpdateDto msgUpdateDto = new RoomMsgUpdateDto();

        msgUpdateDto.roomId = tempRoom.getTempId();
        msgUpdateDto.message = tempRoom.getMessage();
        msgUpdateDto.date = tempRoom.getDate();

        return msgUpdateDto;
    }
}
