package com.sparta.mulmul.service.chat;

import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.dto.NotificationType;
import com.sparta.mulmul.dto.chat.*;
import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.Notification;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.repository.chat.ChatMessageRepository;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository messageRepository;
    private final ChatRoomRepository roomRepository;
    private final NotificationRepository notificationRepository;

    private Map<Long, Integer> roomUsers;

    @PostConstruct
    private void init() {
        roomUsers = new HashMap<>();
    }

    // 채팅방의 상태 전달하기
    public void setConnectedStatus(MessageRequestDto requestDto) {
        // 접속중인 유저의 수를 계산합니다.
        int count = getUserCount(requestDto);
        // 접속 유저의 수에 따른 현재 방의 상태 메시지를 채팅방으로 전달해 줍니다.
        sendRoomStatus(requestDto.getRoomId(), count);
    }

    // 접속중인 유저의 수를 계산하는 메소드
    private int getUserCount(MessageRequestDto requestDto){

        System.out.println("ChatMessageService: 메시지 타입: " + requestDto.getType());
        System.out.println("ChatMessageService: 채팅방 PK: " + requestDto.getRoomId());

        int operator;
        Long roomId = requestDto.getRoomId(); // roomId에 대한 예외처리가 필요합니다.
        MessageTypeEnum type = requestDto.getType(); // 타입에 대한 예외처리가 필요합니다.

        if ( type == MessageTypeEnum.IN ){ operator = 1; }
        else { operator = -1; }

        // 해시맵에 키가 존재한다면 접속중인 사람의 수를 계산합니다.
        if ( roomUsers.containsKey(roomId) ) {

            System.out.println("ChatMessageService: 현재 방의 정원: " + roomUsers.get(roomId));
            int userCount = roomUsers.get(roomId) + operator;

            if (userCount == 0) {
                roomUsers.remove(roomId);
                return 0;
            }
            roomUsers.put(roomId, userCount);
            System.out.println("ChatMessageService: 방 입장: " + roomUsers.get(roomId));
        }
        else { roomUsers.put(roomId, 1); }
        System.out.println("ChatMessageService: 계산결과 방의 정원: " + roomUsers.get(roomId));
        return roomUsers.get(roomId);
    }

    // 현재 방의 정원이 찼는지 전달해 주는 메소드
    private void sendRoomStatus(Long roomId, int count){

        System.out.println("ChatController: /sub/chat/room/" + roomId + " 로 메시지를 전송합니다.");

        if ( count == 2 ){
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    RoomStatusDto.valueOf(MessageTypeEnum.FULL));
            System.out.println("ChatController: 인원 수 " + count + "명으로 집계되었습니다.");
            System.out.println("ChatController: FULL 메시지를 전달합니다.");
        }
        else {
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    RoomStatusDto.valueOf(MessageTypeEnum.NORMAL));
            System.out.println("ChatController: 인원 수 " + count + "명으로 집계되었습니다.");
            System.out.println("ChatController: NORMAL 메시지를 전달합니다.");
        }
    }

    // 메시지 찾기, 페이징 처리
    @Transactional
    public List<MessageResponseDto> getMessage(Long roomId, UserDetailsImpl userDetails){

        // 메시지 찾아오기
        List<ChatMessage> messages = messageRepository.findAllByRoomIdOrderByIdDesc(roomId);

        // responseDto 만들기
        List<MessageResponseDto> responseDtos = new ArrayList<>();

        for (ChatMessage message : messages) {
            // 각 메시지에 대한 유효성 검증 필요
            // isRead 상태 모두 true로 업데이트
            if (!message.getIsRead()){ message.read(); }
            responseDtos.add(MessageResponseDto.createFromChatMessage(message));
        }
        return responseDtos;
    }

    // 채팅 메시지 및 알림 저장하기
    @Transactional
    public MessageResponseDto saveMessage(MessageRequestDto requestDto, WsUser wsUser) {

        ChatRoom chatRoom = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new NullPointerException("ChatController: 해당 채팅방이 존재하지 않습니다."));

        ChatMessage message = messageRepository.save(ChatMessage.createOf(requestDto, wsUser.getUserId()));

        if (chatRoom.getAccOut()){
            // 채팅 알림 저장 및 전달하기 ( 일관성을 위해 수정이 필요합니다. )
            // 알림 메시지도 일관성을 갖춰 제작해야 합니다. ex. xx님이 거래를 요청했습니다. / 가입을 환영합니다. / xx님이 채팅방을 개설했습니다. -> 누르면 해당 방으로 연결됩니다.
            Notification notification = notificationRepository.save(Notification.createOf(
                    chatRoom.getAcceptor().getNickname() + "님에게 채팅이 왔어요!", chatRoom.getAcceptor(), NotificationType.CHAT)
            );
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + chatRoom.getAcceptor().getId(), NotificationDto.createFrom(notification)
            );
            chatRoom.accOut(false);
        }
        if (chatRoom.getReqOut()){
            // 채팅 알림 저장 및 전달하기
            Notification notification = notificationRepository.save(Notification.createOf(
                    chatRoom.getAcceptor().getNickname() + "님에게 채팅이 왔어요!", chatRoom.getRequester(), NotificationType.CHAT)
            );
            messagingTemplate.convertAndSend(
                    "/sub/notification/" + chatRoom.getRequester().getId(), NotificationDto.createFrom(notification)
            );
            chatRoom.reqOut(false);
        }

        return MessageResponseDto.createOf(message, wsUser);
    }

    // 채팅 메시지 발송하기
    public void sendMessage(MessageRequestDto requestDto, WsUser user, MessageResponseDto responseDto){

        RoomMsgUpdateDto msgUpdateDto = RoomMsgUpdateDto.createFrom(requestDto);
        // 발행된 메시지는 sub 프리픽스가 붙은 곳으로 전달됩니다. 클라이언트들이 subscribe 하고 있는 각 sub입니다.
        messagingTemplate.convertAndSend("/sub/chat/rooms/" + user.getUserId(), msgUpdateDto); // 개별 채팅 목록 보기 업데이트
        messagingTemplate.convertAndSend("/sub/chat/room/" + requestDto.getRoomId(), responseDto); // 채팅방 내부로 메시지 전송
        // 첫 메시지인 경우는 알림창으로 전송
    }

}

