package com.sparta.mulmul.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // USER
    NOT_FOUND_USER(HttpStatus.NOT_FOUND.value(), "U001", "해당 사용자를 찾을 수 없습니다."),
    BANNED_USER(HttpStatus.BAD_REQUEST.value(), "U002", "신고횟수 누적으로 차단되어 로그인이 불가능합니다."),

    // CHAT
    NOT_FOUND_CHAT(HttpStatus.NOT_FOUND.value(), "C001", "해당 채팅방을 찾을 수 없습니다."),
    BANNED_CHAT_USER(HttpStatus.BAD_REQUEST.value(), "C002", "차단한 회원과는 채팅을 시도할 수 없습니다."),

    // FILE
    FILE_INVAILED(HttpStatus.BAD_REQUEST.value(), "F001", "잘못된 파일 형식입니다."),

    //ITEM
    NO_MORE_ITEM(HttpStatus.BAD_REQUEST.value(), "I001", "더이상 아이템을 등록할 수 없습니다."),
    CANT_SCRAB_OWN_ITEM(HttpStatus.BAD_REQUEST.value(), "I002", "자신의 아이템은 구독할 수 없습니다."),

    // BARTER
    NOT_FOUND_BARTER(HttpStatus.NOT_FOUND.value(), "B001", "거래내역이 없습니다."),
    FINISH_BARTER(HttpStatus.BAD_REQUEST.value(), "B002", "완료된 거래입니다."),
    NOT_FOUND_SELLER_ITEM(HttpStatus.NOT_FOUND.value(), "B003", "판매자의 상품이 없습니다."),
    NOT_FOUND_BUYER_ITEM(HttpStatus.NOT_FOUND.value(), "B004", "구매자의 상품이 없습니다."),
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND.value(), "B005", "상품이 없습니다."),
    NOT_SCRAB_MY_ITEM(HttpStatus.BAD_REQUEST.value(), "B005", "자신의 상품은 찜할 수 없습니다."),
    NOT_FOUND_SCRAB(HttpStatus.NOT_FOUND.value(), "B005", "찜한 상품이 없습니다."),
    // 성훈 추가
    FINISH_SCORE_BARTER(HttpStatus.BAD_REQUEST.value(), "B002", "완료된 평가입니다."),
    NOT_SCORE_MY_BARTER(HttpStatus.BAD_REQUEST.value(), "B005", "자신에게 평가는 할 수 없습니다."),
    NOT_TRADE_BARTER(HttpStatus.BAD_REQUEST.value(), "B002", "거래중인 상태가 아닙니다."),
    NOT_TRADE_COMPLETE_BARTER(HttpStatus.BAD_REQUEST.value(), "B002", "완료된 거래가 아닙니다."),

    // NOTIFICATION
    NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND.value(), "N001", "알림이 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
