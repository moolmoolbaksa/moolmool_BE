package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.user.UserRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.dto.chat.RoomResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.chat.ChatMessageService;
import com.sparta.mulmul.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService roomService;
    private final ChatMessageService messageService;

    // 채팅방 만들기
    @PostMapping("/room")
    public Long createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody UserRequestDto requestDto) {
        return roomService.createRoom(userDetails, requestDto);
    }

    // 전체 채팅방 목록 가져오기
    @GetMapping("/rooms")
    public List<RoomResponseDto> getRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return roomService.getRooms(userDetails);
    }

    // 개별 채팅방 메시지 불러오기
    @GetMapping("/room/{roomId}")
    public List<MessageResponseDto> getMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long roomId) {
//                                               @PageableDefault(page = 1, size = 20) Pageable pageable) {

        return messageService.getMessage(roomId, userDetails);
    }

    // 채팅방 나가기
    @GetMapping("/room/{roomId}/exit")
    public ResponseEntity<OkDto> exitRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable Long roomId){

        roomService.exitRoom(roomId, userDetails);

        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    // 채팅 즐겨찾기 고정
    @PutMapping("/room/{roomId}")
    public ResponseEntity<OkDto> fixedRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable Long roomId){

        roomService.fixedRoom(roomId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    // 채팅 차단하기
    @GetMapping("/room/{userId}/banned")
    public ResponseEntity<OkDto> setBanned(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @PathVariable Long userId){

        roomService.setBanned(userDetails, userId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

}
