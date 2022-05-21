package com.sparta.mulmul.repository.chat;

import com.sparta.mulmul.dto.chat.UnreadCntDto;

import java.util.List;

public interface MessageQuerydsl {
    List<UnreadCntDto> getUnreadCnts(List<Long> roomIds, Long userId);
}
