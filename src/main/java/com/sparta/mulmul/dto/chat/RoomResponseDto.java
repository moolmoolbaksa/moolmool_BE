package com.sparta.mulmul.dto.chat;

import com.sparta.mulmul.websocket.TempChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomResponseDto {
    private String roomId;
    private Long userId;
    private String nickname;
    private String profile;
    private String message;
    private LocalDateTime date;
    private Boolean isRead;

    public static RoomResponseDto createFrom(TempChatRoom tempRoom){

        RoomResponseDto responseDto = new RoomResponseDto();

        responseDto.roomId = tempRoom.getTempId();
        responseDto.userId = tempRoom.getUserId();
        responseDto.nickname = tempRoom.getNickname();
        responseDto.profile = tempRoom.getProfile();
        responseDto.message = tempRoom.getMessage();
        responseDto.date = tempRoom.getDate();
        responseDto.isRead = tempRoom.getIsRead();

        return responseDto;
    }
}
