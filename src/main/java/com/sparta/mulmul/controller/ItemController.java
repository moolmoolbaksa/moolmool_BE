package com.sparta.mulmul.controller;


import com.sparta.mulmul.dto.ItemRequestDto;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
            @RequestParam("type") String type

    ){
         List<String> imgUrl = awsS3Service.uploadFile(multipartFiles);
        ItemRequestDto itemRequestDto = new ItemRequestDto(category, favored, title, contents, imgUrl, type);
        itemService.createItem(itemRequestDto);
        return "ok";
    }
}
