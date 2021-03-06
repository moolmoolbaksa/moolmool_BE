package com.sparta.mulmul.websocket;

import com.sparta.mulmul.websocket.chatDto.MessageRequestDto;
import com.sparta.mulmul.websocket.chatDto.MessageTypeEnum;
import com.sparta.mulmul.utils.CreationDate;
import com.sparta.mulmul.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.sparta.mulmul.websocket.chatDto.MessageTypeEnum.*;

@Getter @Entity
@NoArgsConstructor
public class ChatMessage extends CreationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    private Long senderId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MessageTypeEnum type;

    @Column(nullable = false)
    private Boolean isRead;

    public static ChatMessage createOf(MessageRequestDto requestDto, Long senderId) {

        ChatMessage message = new ChatMessage();

        message.roomId = requestDto.getRoomId();
        message.senderId = senderId;
        message.message = requestDto.getMessage();
        message.isRead = requestDto.getIsRead();
        message.type = requestDto.getType();

        return message;
    }

    public static ChatMessage createInitOf(Long roomId){

        ChatMessage message = new ChatMessage();

        message.roomId = roomId;
        message.senderId = roomId;
        message.message = "채팅방이 개설되었습니다.";
        message.isRead = true;
        message.type = STATUS;

        return message;
    }

    public static ChatMessage createOutOf(Long roomId, User user){

        ChatMessage message = new ChatMessage();

        message.roomId = roomId;
        message.senderId = roomId;
        message.message = user.getNickname() + "님이 채팅방을 나갔습니다.";
        message.isRead = true;
        message.type = STATUS;

        return message;
    }
}
