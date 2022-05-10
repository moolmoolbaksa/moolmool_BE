package com.sparta.mulmul.controller;

import com.sparta.mulmul.model.ChatRoom;
import com.sparta.mulmul.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ChatRoomRepository roomRepository;

    @GetMapping("/api/test/{num}")
    public int getCount(@PathVariable Long num){

        ChatRoom chatRoom = roomRepository.findById(num).orElseThrow(()-> new NullPointerException("오류"));

        return chatRoom.getUnreadCnt();
    }

}
