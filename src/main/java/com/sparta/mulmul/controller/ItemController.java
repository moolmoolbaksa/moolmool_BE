package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.*;
import com.sparta.mulmul.dto.item.ItemDetailResponseDto;
import com.sparta.mulmul.dto.item.ItemRequestDto;
import com.sparta.mulmul.dto.item.ItemResponseDto;
import com.sparta.mulmul.dto.trade.RequestTradeDto;
import com.sparta.mulmul.dto.trade.TradeDecisionDto;
import com.sparta.mulmul.dto.trade.TradeInfoDto;
import com.sparta.mulmul.dto.user.UserStoreResponseDto;
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
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "favored", required = false) List<String> favored,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "contents", required = false) String contents,
            @RequestParam(value = "images", required = false) List<MultipartFile> multipartFiles,
            @RequestParam(value = "type", required = false) String type,
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

    @GetMapping("/api/store/{userId}")
    private UserStoreResponseDto showStore(@PathVariable Long userId){
        return itemService.showStore(userId);
    }


    // 이승재 / 교환신청하기 전 정보
    @GetMapping("/api/trade")
    private TradeInfoDto showTradeInfo(@RequestParam Long itemId, @RequestParam Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return itemService.showTradeInfo(itemId, userId, userDetails);
    }

    // 이승재 / 교환신청하기 누르면 아이템의 상태 변환 & 거래내역 생성
    @PostMapping("/api/trade")
    private ResponseEntity<OkDto> requestTrade(@RequestBody RequestTradeDto requestTradeDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        itemService.requestTrade(requestTradeDto, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }


    // 이승재 교환신청 확인 페이지
    @GetMapping("/api/trade/decision")
    private TradeDecisionDto tradeDecision(@RequestParam Long baterId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return  itemService.tradeDecision(baterId, userDetails);
    }

    // 이승재 교환신청 확인 페이지 수락 버튼
    @PutMapping("/api/trade/decision")
    private BarterStatusDto acceptTrade(@RequestParam Long baterId){
       return itemService.acceptTrade(baterId);

    }

    //이승재 교환신청 확인 페이지 거절 버튼
    @DeleteMapping("/api/trade/decision")
    private ResponseEntity<OkDto> deleteTrade(@RequestParam Long baterId){
        itemService.deleteTrade(baterId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}