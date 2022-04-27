package com.sparta.mulmul.controller;


import com.sparta.mulmul.dto.BagTestDto;
import com.sparta.mulmul.dto.ItemDetailResponseDto;
import com.sparta.mulmul.dto.ItemRequestDto;
import com.sparta.mulmul.dto.ItemResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.ItemService;
import lombok.RequiredArgsConstructor;
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
    public String  createItem(
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
        return "ok";
    }


    //이승재 / 아이템 전체조회(카테고리별)
    @GetMapping("/items")
    public List<ItemResponseDto> getItems(@RequestParam String category, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getItems(category, userDetails);
    }


    //이승재 / 아이템 상세페이지
    @GetMapping("/api/items/{itemId}")
    public ItemDetailResponseDto getItemDetail(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getItemDetail(itemId, userDetails);
    }

    // 이승재 / 아이템 구독하기
    @PostMapping("/api/{itemId}/scrabs")
    private void scrabItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        itemService.scrabItem(itemId, userDetails);
    }



    // 테스트용 보따리생성
    @PostMapping("/bag")
    public void createBag(@ResponseBody BagTestDto bagTestDto){
        itemService.createBag(bagTestDto);
    }
}
