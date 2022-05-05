package com.sparta.mulmul.service.chat;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private ChatRoomRepository roomRepository;

    public void createRoom(UserDetailsImpl userDetails, UserRequestDto requestDto){

        // UserDetailsImpl은 요청자(Req), UserRequestDto는 수락자(Acc)
        roomRepository.save(ChatRoom.createOfReqAndAcc(userDetails, requestDto));

    }

}
