package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.score.OppentScoreResponseDto;
import com.sparta.mulmul.dto.trade.TradeDecisionDto;
import com.sparta.mulmul.dto.chat.MessageResponseDto;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.ItemService;
import com.sparta.mulmul.service.NotificationService;
import com.sparta.mulmul.service.ScoreService;
import com.sparta.mulmul.service.TradeService;
import com.sparta.mulmul.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final ItemService itemService;
    private final ScoreService scoreService;
    private final ChatMessageService messageService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final TradeService tradeService;

    // 알림 전체 목록 가져오기
    @GetMapping("/notifications")
    public List<NotificationDto> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return notificationService.getNotification(userDetails);
    }

    // 개별 채팅방 메시지 불러오기
    @GetMapping("/notification/{notificationId}/chat")
    public List<MessageResponseDto> getMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable Long notificationId,
                                               @RequestParam Long roomId) {
//                                               @PageableDefault(size = 20) Pageable pageable) {

        List<MessageResponseDto> responseDtos = messageService.getMessage(roomId, userDetails);
        notificationService.setRead(notificationId);
        return responseDtos;
    }

    // 교환신청페이지 연결
    @GetMapping("/notification/{notificationId}/decision")
    private TradeDecisionDto tradeDecision(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @PathVariable Long notificationId,
                                           @RequestParam Long baterId){

        TradeDecisionDto decisionDto = tradeService.tradeDecision(baterId, userDetails);
        notificationService.setRead(notificationId);
        return  decisionDto;
    }

    // 평가 페이지 연결
    @GetMapping("/notification/{notificationId}/score")
    public OppentScoreResponseDto showOppentScore(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long notificationId,
                                                  @RequestParam Long barterId){

        OppentScoreResponseDto responseDto = scoreService.showOppentScore(barterId, userDetails);
        notificationService.setRead(notificationId);
        return responseDto;
    }

    // 알림 삭제하기
    @DeleteMapping("/notification/{notificationId}")
    public ResponseEntity<OkDto> deleteNotification(@PathVariable Long notificationId){

        notificationRepository.deleteById(notificationId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    // 교환 신청 취소
    @GetMapping("/notification/{notificationId}/cancel") // 교환 신청 취소에 대해 이 주소로 요청을 보내면 작동합니다.
    public ResponseEntity<OkDto> cancelBarter(@PathVariable Long notificationId){

        notificationService.setRead(notificationId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    // 회원 가입 메시지
    @GetMapping("/notification/{notificationId}/signup") // 회원가입 축하 메시지에 대해 이 주소로 요청을 보내면 작동합니다.
    public ResponseEntity<OkDto> signup(@PathVariable Long notificationId){

        notificationService.setRead(notificationId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}