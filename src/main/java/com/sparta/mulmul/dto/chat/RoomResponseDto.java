package com.sparta.mulmul.dto.chat;

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
    private int unreadCnt;

    public static RoomResponseDto createOf(ChatRoom chatRoom, ChatMessage message, User user){

        RoomResponseDto responseDto = new RoomResponseDto();

        responseDto.roomId = chatRoom.getId();
        responseDto.userId = user.getId();
        responseDto.nickname = user.getNickname();
        responseDto.profile = user.getProfile();
        // 메시지에 관한 정보도 같이 검색해 와야 한다. 최신 메시지 단 한 건이면 된다.
        responseDto.message = message.getMessage();
        responseDto.date = message.getCreatedAt();
        responseDto.isRead = message.getIsRead();
        responseDto.unreadCnt = chatRoom.getUnreadCnt();

        return responseDto;
    }

}
