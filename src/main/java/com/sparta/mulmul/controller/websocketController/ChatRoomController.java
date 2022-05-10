package com.sparta.mulmul.controller.websocketController;

import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.dto.chat.RoomResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 만들기
    @PostMapping("/room")
    public String createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody UserRequestDto requestDto) {

        return chatRoomService.createRoom(userDetails, requestDto);
    }

    // 전체 채팅방 목록 가져오기
    @GetMapping("/rooms")
    public List<RoomResponseDto> getRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.getRooms(userDetails);
    }

    // 개별 채팅방 메시지 불러오기
    @GetMapping("/room/{roomId}")
    public List<MessageResponseDto> getMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable String roomId,
                                               @RequestParam(value = "page", defaultValue = "1") int page){

        return chatRoomService.getMessage(roomId, page, userDetails);
    }

    // 채팅방 나가기
    @GetMapping("/room/{roomId}/exit")
    public ResponseEntity<OkDto> exitRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable String roomId){

        chatRoomService.exitRoom(roomId, userDetails);

        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

}
