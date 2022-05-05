package com.sparta.mulmul.service.chat;

import com.sparta.mulmul.websocket.TempChatRoom;
import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.dto.chat.*;
import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.repository.chat.ChatMessageRepository;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.websocket.WsUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    private Map<Long, Object> tempRooms;

    @PostConstruct
    private void init(){
        tempRooms = new HashMap<>();
    }

    // 채팅방 만들기
    public void createRoom(UserDetailsImpl userDetails, UserRequestDto requestDto){

        // DB에 채팅방 저장
        ChatRoom chatRoom = roomRepository.save(ChatRoom.createOf(userDetails, requestDto));

        // 채팅 상대 찾아오기
        Long userId = requestDto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new NullPointerException("존재하지 않는 회원입니다."));

        // Hash로 저장될 임시 채팅룸 만들고 기존 해쉬맵 데이터 가져오기
        List<TempChatRoom> temp;

        if ( tempRooms.get(userId) == null ) { temp = new ArrayList<>(); }
        else { temp = (ArrayList<TempChatRoom>) tempRooms.get(userId); }

        // 데이터에 값 추가
        TempChatRoom tempRoom = TempChatRoom.createOf(chatRoom, user);
        temp.add(tempRoom);

        // 해쉬맵 값 변경
        tempRooms.put(userId, tempRooms.get(userId));

        // 업데이트 상황을 구독자에게 전달
        messagingTemplate.convertAndSend("/sub/chat/rooms/" + userDetails.getUserId(), RoomUpdateDto.createOf(tempRoom));
    }

    // 메시지 찾기, 페이징 처리
    @Transactional
    public List<MessageResponseDto> getMessage(String tempId, int page, UserDetailsImpl userDetails){

        // 채팅룸 찾아오기
        List<TempChatRoom> rooms = findRooms(userDetails.getUserId());
        // roomId 찾고 isRead == true로
        Long roomId = getRoomIdAndUpdate(rooms, tempId);

        page -= 1;

        // 페이징 처리
        Pageable pageable = PageRequest.of(page, 50, Sort.by("Id").descending());

        // 메시지 찾아오기
        List<MessageResponseDto> responseDtos = new ArrayList<>();
        List<ChatMessage> messages = messageRepository
                .findAllByRoomId(roomId, pageable);

        for (ChatMessage message : messages) {
            // isRead 상태 모두 true로 업데이트
            if (!message.getIsRead()){ message.read(); }
            responseDtos.add(MessageResponseDto.createFromChatMessage(message));
        }
        return responseDtos;
    }

    // 방을 나간 상태로 변경하기
    public void exitRoom(String tempId, UserDetailsImpl userDetails){

        Long userId = userDetails.getUserId();
        List<TempChatRoom> rooms = findRooms(userId);

        for ( TempChatRoom room : rooms ) {
            if ( room.getTempId().equals(tempId) ) {
                rooms.remove(room);
                if ( rooms.size() == 0 ) { tempRooms.remove(userId); } // 유저가 가진 방이 0이라면 해시에서 삭제
                break;
            }
        }

    }

    // 사용자별 채팅방 전체 목록 가져오기
    public List<RoomResponseDto> getRooms(UserDetailsImpl userDetails){

        // 방 목록 찾기
        Long userId = userDetails.getUserId();
        List<TempChatRoom> rooms = findRooms(userId);

        // responseDto 만들기
        List<RoomResponseDto> responseDtos = new ArrayList<>();
        for (TempChatRoom room : rooms){ responseDtos.add(RoomResponseDto.createFrom(room)); }

        return responseDtos;
    }

    // 채팅 메시지 저장하기
    public MessageResponseDto saveMessage(MessageRequestDto requestDto, WsUser wsUser) {

        // TempChatRoom 찾기
        List<TempChatRoom> rooms = findRooms(wsUser.getUserId());
        // RoomId 찾기
        Long roomId = getRoomId(rooms, requestDto.getRoomId());

        ChatMessage message = messageRepository.save(
                ChatMessage
                        .createOf(requestDto, roomId));

        return MessageResponseDto.createOf(message, wsUser);
    }

    // 채팅 메시지 구독주소로 발송하기
    public void sendMessage(String tempId, Long userId, MessageResponseDto responseDto){

        List<TempChatRoom> rooms = findRooms(userId);
        RoomMsgUpdateDto msgUpdateDto = null;

        for ( TempChatRoom room : rooms ) {
            if ( room.getTempId().equals(tempId) ) {
                room.setMessage(responseDto.getMessage());
                msgUpdateDto = RoomMsgUpdateDto.createFrom(room);
                break;
            }
        }
        if ( msgUpdateDto == null ) { throw new NullPointerException("해당 채팅방을 찾을 수 없습니다."); }

        // 발행된 메시지는 sub 프리픽스가 붙은 곳으로 전달됩니다. 클라이언트들이 subscribe 하고 있는 각 세션입니다.
        messagingTemplate.convertAndSend("/sub/chat/rooms/" + userId, msgUpdateDto);
        messagingTemplate.convertAndSend("/sub/chat/room/" + tempId, responseDto);
    }

    // tempRoom list 찾기
    private List<TempChatRoom> findRooms(Long userId){

        List<TempChatRoom> rooms;

        if ( tempRooms.get(userId) == null ) { throw new NullPointerException("채팅방을 개설하지 않았습니다."); }
        else { rooms = (ArrayList<TempChatRoom>) tempRooms.get(userId); }

        return rooms;

    }

    // roomId 찾기
    private Long getRoomId(List<TempChatRoom> rooms, String tempId){

        for ( TempChatRoom room : rooms ) {
            if ( room.getTempId().equals(tempId) ) {
                return room.getRoomId();
            }
        }
        throw new NullPointerException("해당 채팅방을 찾을 수 없습니다.");
    }

    private Long getRoomIdAndUpdate(List<TempChatRoom> rooms, String tempId){

        for ( TempChatRoom room : rooms ) {
            if ( room.getTempId().equals(tempId) ) {
                room.setIsRead(true);
                return room.getRoomId();
            }
        }
        throw new NullPointerException("해당 채팅방을 찾을 수 없습니다.");
    }

}
