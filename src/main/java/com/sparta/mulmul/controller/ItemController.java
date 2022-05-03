package com.sparta.mulmul.controller;


import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ItemController {

    private final AwsS3Service awsS3Service;
    private final ItemService itemService;


    // 이승재 / 보따리 아이템 등록하기
    @PostMapping("/api/items")
    public ResponseEntity<OkDto> createItem(
            @RequestParam("category") String category,
            @RequestParam("favored") List<String> favored,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents,
            @RequestParam("images") List<MultipartFile> multipartFiles,
            @RequestParam("type") String type,
            @AuthenticationPrincipal UserDetailsImpl userDetails

    ){
        List<String> imgUrl = awsS3Service.uploadFile(multipartFiles);
        ItemRequestDto itemRequestDto = new ItemRequestDto(category, favored, title, contents, imgUrl, type);
        itemService.createItem(itemRequestDto, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }


    //이승재 / 아이템 수정 (미리 구현)
    @PutMapping("/api/items/{itemId}")
    public ResponseEntity<OkDto> updateItem(
            @RequestParam("category") String category,
            @RequestParam("favored") List<String> favored,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents,
            @RequestParam("images") List<MultipartFile> multipartFiles,
            @RequestParam("type") String type,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId

    ){
        List<String> imgUrl = awsS3Service.uploadFile(multipartFiles);
        ItemRequestDto itemRequestDto = new ItemRequestDto(category, favored, title, contents, imgUrl, type);
        itemService.updateItem(itemRequestDto, userDetails, itemId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    //이승재 / 아이템 삭제(미리 구현)
    @DeleteMapping("/api/items/{itemId}")
    public ResponseEntity<OkDto> deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        itemService.deleteItem(itemId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }


    //이승재 / 아이템 전체조회(카테고리별)
    @GetMapping("/items")
    public List<ItemResponseDto> getItems(@RequestParam(required = false) String category, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getItems(category, userDetails);
    }


    //이승재 / 아이템 상세페이지
    @GetMapping("/api/items/{itemId}")
    public ItemDetailResponseDto getItemDetail(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getItemDetail(itemId, userDetails);
    }

    // 이승재 / 아이템 구독하기
    @PostMapping("/api/{itemId}/scrabs")
    private ResponseEntity<OkDto> scrabItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        itemService.scrabItem(itemId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }


    // 이승재 / 유저 스토어 목록 보기

    @GetMapping("/api/{userId}/store")
    private UserStoreResponseDto showStore(@PathVariable Long userId){
        return itemService.showStore(userId);
    }

}