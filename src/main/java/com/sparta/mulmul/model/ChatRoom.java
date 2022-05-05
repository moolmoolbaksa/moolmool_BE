package com.sparta.mulmul.model;

import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter @Entity
@NoArgsConstructor
public class ChatRoom extends CreationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    // PK만 넣지 말고 관련 정보들이 같이 넣어 두는 게 어떨까요?
    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long acceptorId;

    public static ChatRoom createOf(UserDetailsImpl userDetails, UserRequestDto requestDto){

        ChatRoom room = new ChatRoom();

        room.requesterId = userDetails.getUserId();
        room.acceptorId = requestDto.getUserId();

        return room;
    }

}
