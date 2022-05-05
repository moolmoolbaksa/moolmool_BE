package com.sparta.mulmul.controller.websocketController;

import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.UserRequestDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<OkDto> createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @RequestBody UserRequestDto requestDto) {
        chatRoomService.createRoom(userDetails, requestDto);

        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

}
