package com.sparta.mulmul.controller;


import com.sparta.mulmul.dto.BarterStatusDto;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.trade.RequestTradeDto;
import com.sparta.mulmul.dto.trade.TradeDecisionDto;
import com.sparta.mulmul.dto.trade.TradeInfoDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class TradeController {

    private final TradeService tradeService;

    // 이승재 / 교환신청하기 전 정보
    @GetMapping("/api/trade")
    private TradeInfoDto showTradeInfo(@RequestParam Long itemId, @RequestParam Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return tradeService.showTradeInfo(itemId, userId, userDetails);
    }

    // 이승재 / 교환신청하기 누르면 아이템의 상태 변환 & 거래내역 생성
    @PostMapping("/api/trade")
    private ResponseEntity<OkDto> requestTrade(@RequestBody RequestTradeDto requestTradeDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String answer = tradeService.requestTrade(requestTradeDto, userDetails);
        if(answer.equals("true")) {
            return ResponseEntity.ok().body(OkDto.valueOf("true"));
        }else {
            return ResponseEntity.ok().body(OkDto.valueOf("false"));
        }
    }

    // 이승재 교환신청 확인 페이지
    @GetMapping("/api/trade/decision")
    private TradeDecisionDto tradeDecision(@RequestParam Long baterId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return  tradeService.tradeDecision(baterId, userDetails);
    }

    // 이승재 교환신청 확인 페이지 수락 버튼
    @PutMapping("/api/trade/decision")
    private BarterStatusDto acceptTrade(@RequestParam Long baterId){
        return tradeService.acceptTrade(baterId);
    }

    //이승재 교환신청 확인 페이지 거절 버튼
    @DeleteMapping("/api/trade/decision")
    private ResponseEntity<OkDto> deleteTrade(@RequestParam Long baterId){
        tradeService.deleteTrade(baterId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}
