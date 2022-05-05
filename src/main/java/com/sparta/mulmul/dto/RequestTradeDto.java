package com.sparta.mulmul.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RequestTradeDto {
    private Long sellerId;
    private Long sellerItemId;
    private List<Long> buyerItemIds;
}
