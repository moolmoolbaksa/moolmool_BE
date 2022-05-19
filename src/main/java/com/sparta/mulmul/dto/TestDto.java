package com.sparta.mulmul.dto;

import com.sparta.mulmul.model.User;

import java.time.LocalDateTime;

public interface TestDto {

    Long getRoomId();
    User getRequester();
    User getAcceptor();
    Boolean getReqOut();
    Boolean getAccOut();
    Boolean getIsRead();
    Boolean getIsFixed();
    LocalDateTime getModifiedAt();
    Integer getUnreadCnt();

}
