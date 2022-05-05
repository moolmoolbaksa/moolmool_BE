package com.sparta.mulmul.dto.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, STATUS, TALK
    }
    private String roomId;
    private String message;
    private MessageType type;
}
