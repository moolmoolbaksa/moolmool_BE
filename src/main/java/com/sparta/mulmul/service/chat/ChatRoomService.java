package com.sparta.mulmul.service.chat;

import com.sparta.mulmul.dto.RoomDto;
import com.sparta.mulmul.model.ChatMessage;

import com.sparta.mulmul.dto.user.UserRequestDto;
import com.sparta.mulmul.dto.chat.*;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.repository.chat.ChatBannedRepository;
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
    private final ChatBannedRepository bannedRepository;

    // 채팅방 만들기
    public Long createRoom(UserDetailsImpl userDetails, UserRequestDto requestDto){
        // 유효성 검사
        Long acceptorId = requestDto.getUserId();
        if ( userDetails.getUserId() == acceptorId ) {
            throw new IllegalArgumentException("ChatRoomService: createRoom) 채팅 대상은 자기자신이 될 수 없습니다.");
        }
        // 채팅 상대 찾아오기
        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow( () -> new NullPointerException("ChatRoomService: createRoom) 존재하지 않는 회원입니다.")
                );
        User requester = userRepository.findById(userDetails.getUserId())
                .orElseThrow( () -> new NullPointerException("ChatRoomService: createRoom) 존재하지 않는 회원입니다.")
                );
        // 채팅방 차단 회원인지 검색
        if (bannedRepository.existsByUser(acceptor, requester)) {
            throw new AccessDeniedException("ChatRoomService: 차단한 회원과는 채팅을 시도할 수 없습니다.");
        }
        // 채팅방을 찾아보고, 없을 시 DB에 채팅방 저장
        ChatRoom chatRoom = roomRepository.findByUser(requester, acceptor)
                        .orElseGet( () -> {
                            ChatRoom c = roomRepository.save(ChatRoom.createOf(requester, acceptor));
                            messageRepository.save(ChatMessage.createInitOf(c.getId())); // 채팅방 개설 메시지 생성
                            return c;
                        });
        return chatRoom.getId();
    }

    // 방을 나간 상태로 변경하기
    @Transactional
    public void exitRoom(Long id, UserDetailsImpl userDetails){
        // 회원 찾기
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new NullPointerException("ChatRoomService: 해당 회원이 존재하지 않습니다.")
                );
        // 채팅방 찾아오기
        ChatRoom chatRoom = roomRepository.findById(id)
                .orElseThrow(() -> new NullPointerException("ChatRoomService: 해당 채팅방이 존재하지 않습니다.")
                );
        if ( chatRoom.getRequester() == user) { chatRoom.reqOut(true); }
        else if ( chatRoom.getAcceptor() == user) { chatRoom.accOut(true); }
        else { throw new AccessDeniedException("ChatRoomService: '나가기'는 채팅방에 존재하는 회원만 접근 가능한 서비스입니다."); }
        // 채팅방 종료 메시지 전달 및 저장
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(),
                MessageResponseDto.createFrom(
                        messageRepository.save(ChatMessage.createOutOf(id, user))
                )
        );
    }

    // 사용자별 채팅방 전체 목록 가져오기
    public List<RoomResponseDto> getRooms(UserDetailsImpl userDetails){
        // 회원 찾기
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow( () -> new NullPointerException("ChatRoomService: getRooms) 존재하지 않는 회원입니다.")
                );
        // 방 목록 찾기
        List<RoomDto> dtos = roomRepository.findAllWith(user);
        // 메시지 리스트 만들기
        return getMessages(dtos, userDetails.getUserId());
    }

    // 채팅방 즐겨찾기 추가
    @Transactional
    public void fixedRoom(Long roomId, UserDetailsImpl userDetails){

        // fetchJoin 필요
        ChatRoom chatRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("ChatRoomController: 해당 채팅방이 존재하지 않습니다.")
                );
        String flag;
        if ( chatRoom.getAcceptor().getId() == userDetails.getUserId() ){ flag = "acceptor"; }
        else { flag = "requester"; }

        chatRoom.fixedRoom(flag);
    }

    public List<RoomResponseDto> getMessages(List<RoomDto> roomDtos, Long userId){

        List<RoomResponseDto> prefix = new ArrayList<>();
        List<RoomResponseDto> suffix = new ArrayList<>();

        for (RoomDto dto : roomDtos) {
            // 해당 방의 유저가 나가지 않았을 경우에는 배열에 포함해 줍니다.
            if ( dto.getAccId() == userId ) {
                if (!dto.getAccOut()) { // 만약 Acc(내)가 나가지 않았다면
                    int unreadCnt = messageRepository.countMsg(dto.getReqId(), dto.getRoomId());
                    if (dto.getAccFixed()){ prefix.add(RoomResponseDto.createOf("acceptor", dto, unreadCnt)); }
                    else { suffix.add(RoomResponseDto.createOf("acceptor", dto, unreadCnt)); }
                }
            } else if ( dto.getReqId() == userId ){
                if (!dto.getReqOut()) { // 만약 Req(내)가 나가지 않았다면
                    int unreadCnt = messageRepository.countMsg(dto.getAccId(), dto.getRoomId());
                    if (dto.getReqFixed()){ prefix.add(RoomResponseDto.createOf("requester", dto, unreadCnt)); }
                    else { suffix.add(RoomResponseDto.createOf("requester", dto, unreadCnt)); }
                }
            }
        }
        prefix.addAll(suffix);
        return prefix;
    }
}
