package com.sparta.mulmul.service.chat;

import com.sparta.mulmul.dto.chat.MessageRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.dto.chat.MessageTypeEnum;
import com.sparta.mulmul.dto.chat.RoomStatusDto;
import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.repository.chat.ChatMessageRepository;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    private Map<Long, Integer> roomUsers;

    @PostConstruct
    private void init() {
        roomUsers = new HashMap<>();
    }

    // 채팅 메시지 저장하기
    public MessageResponseDto saveMessage(MessageRequestDto requestDto, WsUser wsUser) {

        requestDto.setUserId(wsUser.getUserId());

        ChatMessage message = chatMessageRepository.save(
                ChatMessage
                .fromMessageRequestDto(requestDto));

        return MessageResponseDto.createOf(message, wsUser);
    }

    // 채팅 메시지 구독주소로 발송하기
    public void sendMessage(MessageRequestDto requestDto, MessageResponseDto responseDto){

        Long roomId = requestDto.getRoomId();
        // 발행된 메시지는 sub 프리픽스가 붙은 곳으로 전달됩니다. 클라이언트들이 subscribe 하고 있는 각 세션입니다.
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, responseDto);
    }

    // 채팅방의 상태 전달하기
    public void setConnectedStatus(MessageRequestDto requestDto) {
        // 접속중인 유저의 수를 계산합니다.
        int count = getUserCount(requestDto);
        // 현재 방의 정원이 찼는지 전달해 줍니다.
        sendRoomStatus(requestDto.getRoomId(), count);
    }

    // 접속중인 유저의 수를 계산하는 메소드
    private int getUserCount(MessageRequestDto requestDto){

        int operator;
        Long roomId = requestDto.getRoomId(); // roomId에 대한 예외처리가 필요합니다.
        MessageTypeEnum type = requestDto.getType(); // 타입에 대한 예외처리가 필요합니다.

        if ( type == MessageTypeEnum.IN ){ operator = 1; }
        else { operator = -1; }

        // 해시맵에 키가 존재한다면 접속중인 사람의 수를 계산합니다.
        if ( roomUsers.containsKey(roomId) ) {

            int userCount = roomUsers.get(roomId) + operator;

            if (userCount == 0) {
                roomUsers.remove(roomId);
                return 0;
            }
            roomUsers.put(roomId, userCount);
        }
        else { roomUsers.put(roomId, 1); }

        return roomUsers.get(roomId);
    }

    // 현재 방의 정원이 찼는지 전달해 주는 메소드
    private void sendRoomStatus(Long roomId, int count){

        if ( count == 2 ){
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    RoomStatusDto.valueOf(MessageTypeEnum.FULL)); }
        else {
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    RoomStatusDto.valueOf(MessageTypeEnum.NORMAL)); }
    }

}

