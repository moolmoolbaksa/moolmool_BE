package com.sparta.mulmul.websocket.chat;

import com.sparta.mulmul.websocket.chatDto.NotificationDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.websocket.ChatMessage;
import com.sparta.mulmul.websocket.ChatRoom;
import com.sparta.mulmul.websocket.Notification;
import com.sparta.mulmul.websocket.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.utils.LanguageFilter;
import com.sparta.mulmul.websocket.chatDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sparta.mulmul.websocket.chatDto.MessageTypeEnum.*;
import static com.sparta.mulmul.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final LanguageFilter filter;
    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    private Map<Long, Integer> roomUsers;

    @PostConstruct
    private void init() {
        roomUsers = new HashMap<>();
    }

    // 채팅방의 상태 전달하기
    public void sendStatus(MessageRequestDto requestDto) {

        MessageTypeEnum type;
        int count = getUserCount(requestDto); // 현재 채팅방에 접속중인 유저의 수

        if ( count == 2 ){ type = FULL; }
        else { type = NORMAL; }

        messagingTemplate.convertAndSend("/sub/chat/room/" + requestDto.getRoomId(),
                RoomStatusDto.valueOf(type));
    }

    // 접속중인 유저의 수를 계산하는 메소드
    private int getUserCount(MessageRequestDto requestDto){

        int num;
        Long roomId = requestDto.getRoomId(); // roomId에 대한 예외처리가 필요합니다.

        switch (requestDto.getType()){
            case IN: num = 1; break;
            case OUT: num = -1; break;
            default: throw new IllegalArgumentException("ChatMessageService: 검증메시지 IN과 OUT만 허용됩니다.");
        }
        // 해시맵에 키가 존재한다면 접속중인 사람의 수를 계산합니다.
        if ( roomUsers.containsKey(roomId) ) {

            int userCount = roomUsers.get(roomId) + num;
            if (userCount == 0) {
                roomUsers.remove(roomId);
                return 0;
            }
            roomUsers.put(roomId, userCount);
        }  else { roomUsers.put(roomId, 1); }

        return roomUsers.get(roomId);
    }

    // 메시지 찾기, 페이징 처리 (검증이 필요합니다.)
//    @Cacheable(cacheNames = "chatInfo")
    public List<MessageResponseDto> getMessage(Long roomId, UserDetailsImpl userDetails){
        // 메시지 찾아오기
        List<ChatMessage> messages = messageRepository.findAllByRoomIdOrderByIdDesc(roomId);
        // responseDto 만들기
        List<MessageResponseDto> responseDtos = new ArrayList<>();
        // 상대가 보낸 메시지라면 모두 읽음으로 처리 -> isRead 상태 모두 true로 업데이트
        messageRepository.updateChatMessage(roomId, userDetails.getUserId());

        for (ChatMessage message : messages) {
            responseDtos.add(MessageResponseDto.createFrom(message));
        }
        return responseDtos;
    }

    // 채팅 메시지 및 알림 저장하기
    @Transactional
    public MessageResponseDto saveMessage(MessageRequestDto requestDto, Long userId) {

        ChatRoom chatRoom = roomRepository.findByIdFetch(requestDto.getRoomId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_CHAT));

        // 비속어 필터링
        requestDto = filter.filtering(requestDto);

        ChatMessage message = messageRepository.save(ChatMessage.createOf(requestDto, userId));

        if (chatRoom.getAccOut()){
            // 채팅 알림 저장 및 전달하기
            Notification notification = notificationRepository.save(Notification.createOf(chatRoom, chatRoom.getAcceptor()));
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + chatRoom.getAcceptor().getId(), NotificationDto.createFrom(notification)
            );
            chatRoom.accOut(false);
        }
        if (chatRoom.getReqOut()){
            // 채팅 알림 저장 및 전달하기
            Notification notification = notificationRepository.save(Notification.createOf(chatRoom, chatRoom.getRequester())
            );
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + chatRoom.getRequester().getId(), NotificationDto.createFrom(notification)
            );
            chatRoom.reqOut(false);
        }
        return MessageResponseDto.createOf(message, userId);
    }

    // 채팅 메시지 발송하기
    public void sendMessage(MessageRequestDto requestDto, Long userId, MessageResponseDto responseDto){
        RoomMsgUpdateDto msgUpdateDto = RoomMsgUpdateDto.createFrom(requestDto);
        messagingTemplate.convertAndSend("/sub/chat/rooms/" + userId, msgUpdateDto); // 개별 채팅 목록 보기 업데이트
        messagingTemplate.convertAndSend("/sub/chat/room/" + requestDto.getRoomId(), responseDto); // 채팅방 내부로 메시지 전송
    }
}