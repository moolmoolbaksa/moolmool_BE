package com.sparta.mulmul.dto.trade;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestTradeDto {
    private Long userId;
    private Long itemId;
    private List<Long> myItemIds;
}
