package com.sparta.mulmul.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter @Entity
@NoArgsConstructor
public class ChatRoom extends CreationDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    // PK만 넣지 말고 관련 정보들이 같이 넣어 두는 게 어떨까요?
    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private Long sellerId;

    // DB에서 검색해온 후, show 처리는 서버에서 하는 것이 나을까요?
    @Column(nullable = false)
    private Boolean buyerShow = true;

    @Column(nullable = false)
    private Boolean sellerShow = true;

//    public static ChatRoom

}
