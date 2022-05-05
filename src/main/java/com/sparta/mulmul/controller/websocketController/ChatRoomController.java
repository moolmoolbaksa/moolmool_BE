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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 만들기
    @ResponseBody
    @PostMapping("/room")
    public ResponseEntity<OkDto> createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody UserRequestDto requestDto) {

        chatRoomService.createRoom(userDetails, requestDto);

        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    // 전체 채팅방 목록 가져오기
    @ResponseBody
    @GetMapping("/rooms")
    public List<RoomResponseDto> getRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.getRooms(userDetails);
    }

    // 개별 채팅방 메시지 불러오기
    @ResponseBody
    @GetMapping("/room/{roomId}")
    public List<MessageResponseDto> getMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable String roomId,
                                               @RequestParam("page") int page){

        return chatRoomService.getMessage(roomId, page, userDetails);
    }

    // 채팅방 나가기
    @ResponseBody
    @GetMapping("/room/{roomId}/exit")
    public ResponseEntity<OkDto> exitRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @PathVariable String roomId){

        chatRoomService.exitRoom(roomId, userDetails);

        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

}
