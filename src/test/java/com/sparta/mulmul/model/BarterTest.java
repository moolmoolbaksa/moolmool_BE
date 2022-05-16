//package com.sparta.mulmul.model;
//
//import com.sparta.mulmul.dto.barter.BarterDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class BarterTest {
//
//    @PersistenceContext
//    EntityManager em;
//
//
//    @Nested
//    @DisplayName("회원이 거래내역 객체 생성")
//    class CreateUserProduct {
//
//        private Long id;
//        private Long buyerId;
//        private Long sellerId;
//        private String barterId;
//        private int status = 0;
//        private Boolean isBuyerTrade = false;
//        private Boolean isSellerTrade = false;
//        private Boolean isBuyerScore = false;
//        private Boolean isSellerScore = false;
//        private LocalDateTime tradeTime = null;
//
//
//        @BeforeEach
//        void setup() {
//            id = 1L;
//            buyerId = 1L;
//            sellerId = 2L;
//            barterId = "1,2;4";
//            isBuyerTrade = false;
//            isSellerTrade = false;
//            isBuyerScore = false;
//            isSellerScore = false;
//            tradeTime = null;
//
//        }
//
//        @Test
//        @DisplayName("셀러가 없을 때")
//        void createProduct_Normal() {
//// given
//            Long opponentId = null;
//            User opponentUser = null;
//            String myPosition = "buyer";
//            String myBarterList = barterId.split(";")[0];
//            String barterList = barterId.split(";")[0];
//
//            BarterDto barterDto = new BarterDto(
//                    id,
//                    opponentId,
//                    opponentUser.getNickname(),
//                    opponentUser.getProfile(),
//                    tradeTime,
//                    status,
//                    myPosition,
//                    isBuyerTrade,
//                    isBuyerScore,
//                    null,
//                    null
//            );
//
//// when
////            BarterDto barterDto = new Product(requestDto, userId);
//
//// then
////            assertNull(product.getId());
////            assertEquals(userId, product.getUserId());
////            assertEquals(title, product.getTitle());
////            assertEquals(image, product.getImage());
////            assertEquals(link, product.getLink());
////            assertEquals(lprice, product.getLprice());
////            assertEquals(0, product.getMyprice());
////        }
////
////        @Nested
////        @DisplayName("실패 케이스")
////        class FailCases {
////            @Nested
////            @DisplayName("회원 Id")
////            class userId {
////                @Test
////                @DisplayName("null")
////                void fail1() {
////// given
////                    userId = null;
////
////                    ProductRequestDto requestDto = new ProductRequestDto(
////                            title,
////                            image,
////                            link,
////                            lprice
////                    );
////
////// when
////                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
////                        new Product(requestDto, userId);
////                    });
////
////// then
////                    assertEquals("회원 Id 가 유효하지 않습니다.", exception.getMessage());
////                }
////
////                @Test
////                @DisplayName("마이너스")
////                void fail2() {
////// given
////                    userId = -100L;
////
////                    ProductRequestDto requestDto = new ProductRequestDto(
////                            title,
////                            image,
////                            link,
////                            lprice
////                    );
////
////// when
////                    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
////                        new Product(requestDto, userId);
////                    });
////
////// then
////                    assertEquals("회원 Id 가 유효하지 않습니다.", exception.getMessage());
////                }
////            }
//
//
//        }
//    }
//}