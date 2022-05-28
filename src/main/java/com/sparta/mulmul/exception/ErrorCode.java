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
    CHAT_USER_BANNED(HttpStatus.BAD_REQUEST.value(), "C002", "차단한 회원과는 채팅을 시도할 수 없습니다."),
    NOT_FOUND_BANNED(HttpStatus.NOT_FOUND.value(), "C003", "차단 목록을 찾을 수 없습니다."),
    ALREADY_BANNED(HttpStatus.BAD_REQUEST.value(), "C004", "이미 차단한 회원입니다."),
    NOT_FOUND_REQUESTER(HttpStatus.NOT_FOUND.value(), "C005", "차단을 요청한 회원을 찾을 수 없습니다."),
    EXIT_INVAILED(HttpStatus.BAD_REQUEST.value(), "C006", "'나가기'는 채팅방에 존재하는 회원만 접근 가능한 서비스입니다."),

    // FILE
    FILE_INVAILED(HttpStatus.BAD_REQUEST.value(), "F001", "잘못된 파일 형식입니다."),

    //ITEM
    NO_MORE_ITEM(HttpStatus.BAD_REQUEST.value(), "I001", "더이상 아이템을 등록할 수 없습니다."),
    CANT_SCRAB_OWN_ITEM(HttpStatus.BAD_REQUEST.value(), "I002", "자신의 아이템은 구독할 수 없습니다."),
    EXISTED_ITEM(HttpStatus.BAD_REQUEST.value(), "I003", "이미 아이템이 올라가 있습니다."),

    //IMAGE
    FAILIED_UPLOAD_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "M001", "파일 업로드에 실패하였습니다"),
    FAILIED_RESIZE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "M002", "파일 리사이즈에 실패하였습니다"),
    WRONG_TYPE_IMAGE(HttpStatus.BAD_REQUEST.value(), "M003", "잘못된 형식의 파일입니다"),

    // BARTER
    NOT_FOUND_BARTER(HttpStatus.NOT_FOUND.value(), "B001", "거래내역이 없습니다."),
    FINISH_BARTER(HttpStatus.BAD_REQUEST.value(), "B002", "완료된 거래입니다."),
    NOT_FOUND_SELLER_ITEM(HttpStatus.NOT_FOUND.value(), "B003", "판매자의 상품이 없습니다."),
    NOT_FOUND_BUYER_ITEM(HttpStatus.NOT_FOUND.value(), "B004", "구매자의 상품이 없습니다."),
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND.value(), "B005", "상품이 없습니다."),
    NOT_SCRAB_MY_ITEM(HttpStatus.BAD_REQUEST.value(), "B006", "자신의 상품은 찜할 수 없습니다."),
    NOT_FOUND_SCRAB(HttpStatus.NOT_FOUND.value(), "B007", "찜한 상품이 없습니다."),
    FINISH_SCORE_BARTER(HttpStatus.BAD_REQUEST.value(), "B008", "완료된 평가입니다."),
    NOT_SCORE_MY_BARTER(HttpStatus.BAD_REQUEST.value(), "B009", "자신에게 평가는 할 수 없습니다."),
    NOT_TRADE_BARTER(HttpStatus.BAD_REQUEST.value(), "B010", "거래중인 상태가 아닙니다."),
    NOT_TRADE_COMPLETE_BARTER(HttpStatus.BAD_REQUEST.value(), "B011", "완료된 거래가 아닙니다."),

    // NOTIFICATION
    NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND.value(), "N001", "알림이 없습니다."),

    // LOGIN
    LOGIN_FAILED(HttpStatus.BAD_REQUEST.value(), "L001", "아이디와 비밀번호를 확인해 주세요."),
    INVALID_LENGTH_TOKEN(HttpStatus.BAD_REQUEST.value(), "L002", "유효하지 않은 토큰입니다. 토큰의 길이를 확인해 주세요."),
    DECODING_FAILED_TOKEN(HttpStatus.BAD_REQUEST.value(), "L003", "유효하지 않은 토큰입니다. 토큰 디코딩에 실패했습니다."),
    EXPIRATION_TOKEN(HttpStatus.BAD_REQUEST.value(), "L004", "만료된 토큰입니다."),
    INVAILD_CONTENTS_TOKEN(HttpStatus.BAD_REQUEST.value(), "L005", "유효하지 않은 형식의 토큰입니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
