package com.sparta.mulmul.service.chat;

import com.sparta.mulmul.model.ChatMessage;
import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.dto.chat.*;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.repository.chat.ChatMessageRepository;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository roomRepository;
    private final ChatMessageRepository messageRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    // 채팅방 만들기
    public Long createRoom(UserDetailsImpl userDetails, UserRequestDto requestDto){
        // 유효성 검사
        Long acceptorId = requestDto.getUserId();
        if ( userDetails.getUserId() == acceptorId ) {
            throw new IllegalArgumentException("ChatRoomService: createRoom) 채팅 대상은 자기자신이 될 수 없습니다.");
        }
        // 채팅 상대 찾아오기
        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow( () -> new NullPointerException("ChatRoomService: createRoom) 존재하지 않는 회원입니다."));
        User requester = userRepository.findById(userDetails.getUserId())
                .orElseThrow( () -> new NullPointerException("ChatRoomService: createRoom) 존재하지 않는 회원입니다."));
        // 채팅방을 찾아보고, 없을 시 DB에 채팅방 저장
        ChatRoom chatRoom = roomRepository.findByRequesterAndAcceptor(requester, acceptor)
                .orElse(roomRepository.findByRequesterAndAcceptor(acceptor, requester)
                        .orElse(roomRepository.save(ChatRoom.createOf(requester, acceptor))));
        // 채팅방 개설 메시지 생성
        messageRepository.save(ChatMessage.createInitOf(chatRoom.getId(), chatRoom.getId()));

        return chatRoom.getId();
    }

    // 방을 나간 상태로 변경하기
    @Transactional
    public void exitRoom(Long id, UserDetailsImpl userDetails){
        // 회원 찾기
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new NullPointerException("ChatRoomService: 해당 회원이 존재하지 않습니다."));
        // 채팅방 찾아오기
        ChatRoom chatRoom = roomRepository.findById(id).orElseThrow(()->new NullPointerException("ChatRoomService: 해당 채팅방이 존재하지 않습니다."));
        if ( chatRoom.getRequester() == user) { chatRoom.reqOut(true); }
        else if ( chatRoom.getAcceptor() == user) { chatRoom.accOut(true); }
        else { throw new AccessDeniedException("ChatRoomService: '나가기'는 채팅방에 존재하는 회원만 접근 가능한 서비스입니다."); }
        // 채팅방 종료 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), "상대방이 채팅방을 나갔습니다."); // 세부 내용 수정 필요
    }

    // 사용자별 채팅방 전체 목록 가져오기
    public List<RoomResponseDto> getRooms(UserDetailsImpl userDetails){
        // 회원 찾기
        Long userId = userDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow( () -> new NullPointerException("ChatRoomService: getRooms) 존재하지 않는 회원입니다."));
        // 방 목록 찾기
        List<ChatRoom> chatRooms = roomRepository.findAllBy(user);
        // responseDto 만들기
        List<RoomResponseDto> responseDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms ){
            // 메시지 목록 가져오기
            ChatMessage message = messageRepository.findFirstByRoomIdOrderByIdDesc(chatRoom.getId())
                    .orElseThrow( () -> new IllegalArgumentException("ChatRoomService: 채팅방 메시지를 설정해 주지 않았습니다. 방 개설과 함께 채팅 메시지를 작성하도록 설정하세요."));
            // 해당 방의 유저가 나가지 않았을 경우에는 배열에 포함해 줍니다.
            if ( chatRoom.getAcceptor().getId() == userId ) {
                if (!chatRoom.getAccOut()) { // 만약 Acc가 나가지 않았다면
                    responseDtos.add(RoomResponseDto.createOf(chatRoom, message, chatRoom.getRequester())); }
            } else if ( chatRoom.getRequester().getId() == userId ){
                if (!chatRoom.getReqOut()) { // 만약 Req가 나가지 않았다면
                    responseDtos.add(RoomResponseDto.createOf(chatRoom, message, chatRoom.getAcceptor())); }
            }
        }
        return responseDtos;
    }

}
