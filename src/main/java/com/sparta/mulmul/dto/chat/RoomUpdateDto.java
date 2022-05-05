package com.sparta.mulmul.dto.chat;

import com.sparta.mulmul.websocket.TempChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomUpdateDto {

    private String roomId;
    private Long userId;
    private String nickname;
    private String profile;
    private String message;
    private LocalDateTime date;
    private Boolean isRead;

    public static RoomUpdateDto createOf(TempChatRoom tempRoom){

        RoomUpdateDto updateDto = new RoomUpdateDto();

        updateDto.roomId = tempRoom.getTempId();
        updateDto.userId = tempRoom.getUserId();
        updateDto.nickname = tempRoom.getNickname();
        updateDto.profile = tempRoom.getProfile();
        updateDto.message = tempRoom.getMessage();
        updateDto.date = tempRoom.getDate();
        updateDto.isRead = tempRoom.getIsRead();

        return updateDto;
    }

}
