package com.sparta.mulmul.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UnreadCntDto {
    private Long roomId;
    private Long unreadCnt;
}
