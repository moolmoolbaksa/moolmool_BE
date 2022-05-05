package com.sparta.mulmul.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class RoomDto {

    private String roomId;
    private String name;

    public static RoomDto create(String name) {

        RoomDto room = new RoomDto();
        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;

    }
}