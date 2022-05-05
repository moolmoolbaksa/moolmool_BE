package com.sparta.mulmul.websocket;

import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
public class TempChatRoom {

    private String tempId;
    private Long userId; // 대화상대의 아이디
    private String nickname; // 대화상대의 닉네임
    private Long roomId; // DB에 저장되는 실제 채팅방 PK
    private String profile; // 대화상대의 프로필 사진
    private String message;
    private LocalDateTime date;
    private Boolean isRead;

    public static TempChatRoom createOf(ChatRoom chatRoom, User user){

        TempChatRoom tempRoom = new TempChatRoom();

        tempRoom.tempId = UUID.randomUUID().toString();
        tempRoom.userId = chatRoom.getAcceptorId();
        tempRoom.roomId = chatRoom.getId();
        tempRoom.profile = user.getProfile();
        tempRoom.nickname = user.getNickname();
        tempRoom.date = chatRoom.getCreatedAt();
        tempRoom.message = "채팅방이 개설되었습니다.";
        tempRoom.isRead = false;

        return tempRoom;
    }

}
