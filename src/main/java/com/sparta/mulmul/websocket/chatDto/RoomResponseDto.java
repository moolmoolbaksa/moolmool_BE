package com.sparta.mulmul.websocket.chatDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.sparta.mulmul.websocket.chat.ChatRoomService.UserTypeEnum.Type.ACCEPTOR;
import static com.sparta.mulmul.websocket.chat.ChatRoomService.UserTypeEnum.Type.REQUESTER;

@Getter
@NoArgsConstructor
public class RoomResponseDto {

    private Long roomId;
    private Long userId;
    private String nickname;
    private String profile;
    private String message;
    private LocalDateTime date;
    private Boolean isRead;
    private Boolean isBanned;
    private int unreadCnt;

    public static RoomResponseDto createOf(String flag, RoomDto dto, int unreadCnt, Boolean isBanned){

        RoomResponseDto responseDto = new RoomResponseDto();

        responseDto.roomId = dto.getRoomId();
        responseDto.message = dto.getMessage();
        responseDto.date = dto.getDate();
        responseDto.isRead = dto.getIsRead();
        responseDto.isBanned = isBanned;
        responseDto.unreadCnt = unreadCnt;

        switch ( flag ) {

            case ACCEPTOR:

                responseDto.userId = dto.getReqId();
                responseDto.nickname = dto.getReqNickname();
                responseDto.profile = dto.getReqProfile();
                break;

            case REQUESTER:

                responseDto.userId = dto.getAccId();
                responseDto.nickname = dto.getAccNickname();
                responseDto.profile = dto.getAccProfile();
                break;

            default: break;
        }

        return responseDto;
    }
}
