package com.sparta.mulmul.trade;


import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.trade.tradeDto.RequestTradeDto;
import com.sparta.mulmul.trade.tradeDto.TradeDecisionDto;
import com.sparta.mulmul.trade.tradeDto.TradeInfoDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class TradeController {

    private final TradeService tradeService;

    // 이승재 / 교환신청하기 전 정보
    @GetMapping("/items/trade")
    private TradeInfoDto showTradeInfo(@RequestParam Long itemId, @RequestParam Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return tradeService.showTradeInfo(itemId, userId, userDetails);
    }

    // 이승재 / 교환신청하기 누르면 아이템의 상태 변환 & 거래내역 생성
    @PostMapping("/itmes/trade")
    private ResponseEntity<OkDto> requestTrade(@RequestBody RequestTradeDto requestTradeDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        String answer = tradeService.requestTrade(requestTradeDto, userDetails);
        if(answer.equals("true")) {
            return ResponseEntity.ok().body(OkDto.valueOf("true"));
        }else {
            return ResponseEntity.ok().body(OkDto.valueOf("false"));
        }
    }

    // 이승재 교환신청 확인 페이지
    @GetMapping("/items/trade/decision")
    private TradeDecisionDto tradeDecision(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return  tradeService.tradeDecision(barterId, userDetails);
    }

    // 이승재 교환신청 확인 페이지 수락 버튼
    @PutMapping("/items/trade/decision")
    private BarterStatusDto acceptTrade(@RequestParam Long barterId){
        return tradeService.acceptTrade(barterId);
    }

    //이승재 교환신청 확인 페이지 거절 버튼
    @DeleteMapping("/items/trade/decision")
    private ResponseEntity<OkDto> deleteTrade(@RequestParam Long barterId){
        tradeService.deleteTrade(barterId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}
