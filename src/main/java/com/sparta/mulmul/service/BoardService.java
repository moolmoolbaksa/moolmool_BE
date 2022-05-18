//package com.sparta.mulmul.service;
//
//import com.sparta.mulmul.dto.BoardRequestDto;
//import com.sparta.mulmul.model.Post;
//import com.sparta.mulmul.model.User;
//import com.sparta.mulmul.repository.*;
//import com.sparta.mulmul.security.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BoardService {
//
//
//    private final ItemRepository itemRepository;
//    private final BagRepository bagRepository;
//    private final PostRepository postRepository;
//    private final ScrabRepository scrabRepository;
//    private final UserRepository userRepository;
//    private final BarterRepository barterRepository;
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final NotificationRepository notificationRepository;
//
//
//    //엄성훈 / 게시판 등록하기
//    public void createBoard(BoardRequestDto boardRequestDto, UserDetailsImpl userDetails) {
//
//        System.out.println(userDetails.getUsername());
//        User user = userRepository.getById(userDetails.getUserId());
//        List<String> imgUrlList = boardRequestDto.getImgUrl();
//        String imgUrl = String.join(",", imgUrlList);
//
//        Post post = Post.builder()
//                .userId(user.getId())
//                .nickname(user.getNickname())
//                .title(boardRequestDto.getTitle())
//                .contents(boardRequestDto.getContents())
//                .likeCnt(0)
//                .commentCnt(0)
//                .viewCnt(0)
//                .reportCnt(0)
//                .status(0)
//                .itemImg(imgUrl)
//                .build();
//
//        postRepository.save(post);
//    }
//
//
//}
//
