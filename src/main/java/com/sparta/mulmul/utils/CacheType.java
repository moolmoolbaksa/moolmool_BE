package com.sparta.mulmul.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    USER_PROFILE("userProfile", 30, 10000),

    ITEM_INFO("itemInfo", 2, 10000),
    ITEM_DETAIL_INFO("itemDetailInfo", 5, 10000),
    ITEM_TRADE_CHECK_INFO("itemTradeCheckInfo", 10, 10000),

    BARTER_MY_INFO("barterMyInfo", 2, 10000);


    // 캐시 이름, 만료 시간, 저장 가능한 최대 갯수
    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;

}