package com.sparta.mulmul.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    USER_PROFILE("userProfile", 30, 10000),
    USER_BAN("userBan", 10, 10000),
    USER_SCORE("userScore", 10, 10000),
    ANOTHER_USER_PROFILE("anotherUserProfile", 30, 10000),


    CHAT_INFO("chatInfo", 1, 10000),
    CHAT_LIST_INFO("chatListInfo", 2, 10000),
    NOTIFICATION_INFO("notificationInfo", 5, 10000),

    ITEM_INFO("itemInfo", 10, 10000),
    ITEM_DETAIL_INFO("itemDetailInfo", 10, 10000),
    HOT_ITEM_INFO("hotItemInfo", 10, 10000),
    ITEM_SEARCH_INFO("itemSearchInfo", 5, 10000),
    ITEM_TRADE_INFO("itemTradeInfo", 10, 10000),
    ITEM_TRADE_CHECK_INFO("itemTradeCheckInfo", 10, 10000),
    SCRAB_ITEM_INFO("scrabItemInfo", 10, 10000),


    BARTER_My_INFO("barterMyInfo", 5, 10000),
    BARTER_INFO("barterInfo", 5, 10000);



    // 캐시 이름, 만료 시간, 저장 가능한 최대 갯수
    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;

}