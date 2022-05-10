package com.sparta.mulmul.service.chat;

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

    private Map<Long, Object> map;

    @PostConstruct
    private void init(){
        map = new HashMap<>();
    }

    // 채팅방 만들기
    public String createRoom(UserDetailsImpl userDetails, UserRequestDto requestDto){

        // 채팅 상대 찾아오기
        Long opponentId = requestDto.getUserId();
        if ( userDetails.getUserId() == opponentId ) {
            throw new IllegalArgumentException("채팅 대상은 자기자신이 될 수 없습니다.");
        }
        User opponentUser = userRepository.findById(opponentId)
                .orElseThrow( () -> new NullPointerException("존재하지 않는 회원입니다."));

        // 채팅방 찾아오기
        List<TempChatRoom> rooms = findRooms(userDetails.getUserId());

        // 채팅방 중복 검사
        for ( TempChatRoom room : rooms ) {
            if ( room.getUserId() == requestDto.getUserId() ) { return room.getTempId(); } // 기존의 방의 고유번호를 리턴
        }

        // DB에 채팅방 저장
        ChatRoom chatRoom = roomRepository.save(ChatRoom.createOf(userDetails, requestDto));

        // Hash로 저장될 임시 채팅룸 만들고 기존 해쉬맵 데이터 가져오기
        if ( map.get(userDetails.getUserId()) == null ) { rooms = new ArrayList<>(); }
        else { rooms = (ArrayList<TempChatRoom>) map.get(userDetails.getUserId()); }

        // 데이터에 값 추가
        TempChatRoom tempRoom = TempChatRoom.createOf(chatRoom, opponentUser);
        rooms.add(tempRoom); // ArrayList의 add는 Boolean 값을 반환합니다.

        // 해쉬맵 값 변경
        map.put(userDetails.getUserId(), rooms);

        System.out.println("채팅방 저장) 방 사이즈 증가를 확인합니다.: " + rooms.size());

        return tempRoom.getTempId();

    }

    // 메시지 찾기, 페이징 처리
    @Transactional
    public List<MessageResponseDto> getMessage(String tempId, int page, UserDetailsImpl userDetails){

        // 채팅방 찾아오기
        List<TempChatRoom> rooms = findRooms(userDetails.getUserId());
        if ( rooms.size() < 1 ) { throw new IllegalArgumentException("해당 채팅방이 없습니다.");}

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
                // 유저가 가진 방이 0이라면 해시에서 삭제
                if ( rooms.size() == 0 ) { map.remove(userId); }
                // DB 정보 업데이트 -> 방에서 나간 상태로

                break;
            }
        }
    }

    // 사용자별 채팅방 전체 목록 가져오기
    public List<RoomResponseDto> getRooms(UserDetailsImpl userDetails){

        // 방 목록 찾기
        Long userId = userDetails.getUserId();
        List<TempChatRoom> rooms = findRooms(userId);
        System.out.println("채팅방 찾기) 채팅방의 사이즈 : " + rooms.size());

        // responseDto 만들기
        List<RoomResponseDto> responseDtos = new ArrayList<>();
        for (TempChatRoom room : rooms){
            System.out.println("채팅방의 메시지 : " + room.getMessage()); // 채팅방이 개설되었습니다.
            responseDtos.add(RoomResponseDto.createFrom(room));
        }

        // 검증용 for문입니다.
        for (RoomResponseDto responseDto : responseDtos ){
            System.out.println("닉네임 : " + responseDto.getNickname());
        }

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
                        .createOf(requestDto, roomId, wsUser.getUserId()));

        return MessageResponseDto.createOf(message, wsUser);
    }

    // 채팅 메시지 발송하기
    public void sendMessage(String tempId, Long userId, MessageResponseDto responseDto){

        List<TempChatRoom> rooms = findRooms(userId);
        RoomMsgUpdateDto msgUpdateDto = null;

        for ( TempChatRoom room : rooms ) {
            if ( room.getTempId().equals(tempId) ) {
                room.setMessage(responseDto.getMessage()); // 메시지
                room.setDate(responseDto.getDate()); // 메시지 발신 날자
                msgUpdateDto = RoomMsgUpdateDto.createFrom(room);
                break;
            }
        }
        if ( msgUpdateDto == null ) { throw new NullPointerException("해당 채팅방을 찾을 수 없습니다."); }

        // 발행된 메시지는 sub 프리픽스가 붙은 곳으로 전달됩니다. 클라이언트들이 subscribe 하고 있는 각 sub입니다.
        messagingTemplate.convertAndSend("/sub/chat/rooms/" + userId, msgUpdateDto);
        messagingTemplate.convertAndSend("/sub/chat/room/" + tempId, responseDto);
        // 채팅 상대가 방이 없다면, 이 단계에서 만들어 줘야 합니다.
//        if ( map.get(userDetails.getUserId()) == null ) { rooms = new ArrayList<>(); }
//        else { rooms = (ArrayList<TempChatRoom>) map.get(userDetails.getUserId()); }
//
//        // 데이터에 값 추가
//        TempChatRoom tempRoom = TempChatRoom.createOf(chatRoom, opponentUser);
//        rooms.add(tempRoom); // ArrayList의 add는 Boolean 값을 반환합니다.
//
//        // 해쉬맵 값 변경
//        map.put(userDetails.getUserId(), rooms);
//
//        System.out.println("채팅방 저장) 방 사이즈 증가를 확인합니다.: " + rooms.size());


    }

    // tempRoom list 찾기
    private List<TempChatRoom> findRooms(Long userId){
        System.out.println("find) 채팅룸을 찾는 이용자의 아이디 : " + userId);
        List<TempChatRoom> rooms;

        if ( map.get(userId) == null ) { rooms = new ArrayList<>(); } // null 체크는 반드시 해야함
        else { rooms = (ArrayList<TempChatRoom>) map.get(userId); }

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
