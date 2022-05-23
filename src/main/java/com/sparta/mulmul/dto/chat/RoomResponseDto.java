package com.sparta.mulmul.dto.chat;

import com.sparta.mulmul.dto.RoomDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.sparta.mulmul.service.chat.ChatRoomService.UserTypeEnum.Type.ACCEPTOR;
import static com.sparta.mulmul.service.chat.ChatRoomService.UserTypeEnum.Type.REQUESTER;

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
    private Long isBanned;
    private long unreadCnt;

    public static RoomResponseDto createOf(String flag, RoomDto dto, long unreadCnt){

        RoomResponseDto responseDto = new RoomResponseDto();

        responseDto.roomId = dto.getRoomId();
        responseDto.message = dto.getMessage();
        responseDto.date = dto.getDate();
        responseDto.isRead = dto.getIsRead();
        responseDto.isBanned = dto.getIsBanned();
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
