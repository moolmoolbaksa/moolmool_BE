package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.item.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.AwsS3Service;
import com.sparta.mulmul.service.ItemService;
import com.sparta.mulmul.service.ItemStarService;
import com.sparta.mulmul.service.MyUserService;
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
    private final ItemStarService itemStarService;
    private final MyUserService myUserService;


    // 이승재 / 보따리 아이템 등록하기
    @PostMapping("/api/items")
    public Long createItem(
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
        return itemService.createItem(itemRequestDto, userDetails);

    }


    //이승재 / 아이템 수정 (미리 구현)
    @PutMapping("/api/items/{itemId}")
    public ResponseEntity<OkDto> updateItem(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "contents", required = false) String contents,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "favored", required = false) List<String> favored,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "imagesUrl", required = false) List<String> imagesUrl,
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long itemId

    ){
            List<String> images = awsS3Service.uploadFile(multipartFiles);

        ItemUpdateRequestDto itemUpdateRequestDto = new ItemUpdateRequestDto(category, favored, title, contents, imagesUrl,images, type);
        itemService.updateItem(itemUpdateRequestDto, userDetails, itemId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    //이승재 / 아이템 삭제(미리 구현)
    @DeleteMapping("/api/items/{itemId}")
    public ResponseEntity<OkDto> deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        itemService.deleteItem(itemId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }


    //이승재 / 아이템 전체조회(카테고리별)
    @GetMapping("/items/{pageNo}")
    public ItemMainResponseDto getItems(@PathVariable int pageNo, @RequestParam(required = false) String category, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.getItems(pageNo, category, userDetails);
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


    // 이승재 아이템 신고하기
    @PutMapping("/api/report/item")
    private ResponseEntity<OkDto> reportItem(@RequestParam Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String answer = itemService.reportItem(itemId, userDetails);
        if(answer.equals("true")) {
            return ResponseEntity.ok().body(OkDto.valueOf("true"));
        }else{
            return ResponseEntity.ok().body(OkDto.valueOf("false"));
        }
    }

    // 이승재 아이템 검색하기
    @GetMapping("/api/item/search")
    private List<ItemResponseDto> searchItem(@RequestParam String keyword, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.searchItem(keyword, userDetails);
    }

    // 이승재 교환신청 확인 페이지
    @GetMapping("/items/star")
    public List<ItemStarDto> hotItem(){
        return  itemStarService.hotItem();
    }
}