//package com.sparta.mulmul.controller;
//
//import com.sparta.mulmul.dto.BoardRequestDto;
//import com.sparta.mulmul.dto.OkDto;
//import com.sparta.mulmul.dto.item.ItemRequestDto;
//import com.sparta.mulmul.security.UserDetailsImpl;
//import com.sparta.mulmul.service.AwsS3Service;
//import com.sparta.mulmul.service.BoardService;
//import com.sparta.mulmul.service.MyUserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RequiredArgsConstructor
//@RestController
//public class BoardController {
//
//    private final AwsS3Service awsS3Service;
//    private final MyUserService myUserService;
//    private final BoardService boardService;
//
//
//    // 엄성훈 / 게시판 등록하기
//    @PostMapping("/api/board")
//    public ResponseEntity<OkDto> createBorad(
//            @RequestParam("title") String title,
//            @RequestParam("contents") String contents,
//            @RequestParam("images") List<MultipartFile> multipartFiles,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ){
//        List<String> imgUrl = awsS3Service.uploadFile(multipartFiles);
//        BoardRequestDto boardRequestDto = new BoardRequestDto(title, contents, imgUrl);
//        boardService.createBoard(boardRequestDto, userDetails);
//        return ResponseEntity.ok().body(OkDto.valueOf("true"));
//    }
//
//
//}
