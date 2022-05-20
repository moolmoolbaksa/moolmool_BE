package com.sparta.mulmul.dto.chat;

import com.sparta.mulmul.dto.RoomDto;
import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private Boolean isBanned = false;
    private int unreadCnt;

//    public static RoomResponseDto createOf(ChatRoom chatRoom, ChatMessage message, User user, int unreadCnt, boolean exist){
//
//        RoomResponseDto responseDto = new RoomResponseDto();
//
//        responseDto.roomId = chatRoom.getId();
//        responseDto.userId = user.getId();
//        responseDto.nickname = user.getNickname();
//        responseDto.profile = user.getProfile();
//        // 메시지에 관한 정보도 같이 검색해 와야 한다. 최신 메시지 단 한 건이면 된다.
//        responseDto.message = message.getMessage();
//        responseDto.date = message.getCreatedAt();
//        responseDto.isRead = message.getIsRead();
//        responseDto.unreadCnt = unreadCnt;
//        if (exist) { responseDto.isBanned = true; }
//
//        return responseDto;
//    }

    public static RoomResponseDto createOf(String flag, RoomDto dto, int unreadCnt){

        RoomResponseDto responseDto = new RoomResponseDto();

        responseDto.roomId = dto.getRoomId();
        responseDto.message = dto.getMessage();
        responseDto.date = dto.getDate();
        responseDto.isRead = dto.getIsRead();
        responseDto.isBanned = dto.getIsBanned();
        responseDto.unreadCnt = unreadCnt;

        switch ( flag ) {

            case "acceptor" :

                responseDto.userId = dto.getReqId();
                responseDto.nickname = dto.getReqNickname();
                responseDto.profile = dto.getReqProfile();
                break;

            case "requester" :

                responseDto.userId = dto.getAccId();
                responseDto.nickname = dto.getAccNickname();
                responseDto.profile = dto.getAccProfile();
                break;

            default: break;
        }

        return responseDto;
    }
}
