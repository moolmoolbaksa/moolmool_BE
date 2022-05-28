package com.sparta.mulmul.websocket;

import com.sparta.mulmul.websocket.chatDto.NotificationDto;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.websocket.chatDto.MessageResponseDto;
import com.sparta.mulmul.barter.scoreDto.OppentScoreResponseDto;
import com.sparta.mulmul.trade.tradeDto.TradeDecisionDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.barter.ScoreService;
import com.sparta.mulmul.trade.TradeService;
import com.sparta.mulmul.websocket.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class NotificationController {

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
    @GetMapping("/notification/chat")
    public List<MessageResponseDto> getMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @RequestParam Long notificationId,
                                               @RequestParam Long roomId) {
//                                               @PageableDefault(size = 20) Pageable pageable) {

        List<MessageResponseDto> responseDtos = messageService.getMessage(roomId, userDetails);
        notificationService.setRead(notificationId);
        return responseDtos;
    }

    // 교환신청페이지 연결
    @GetMapping("/notification/decision")
    private TradeDecisionDto tradeDecision(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestParam Long notificationId,
                                           @RequestParam Long baterId){

        TradeDecisionDto decisionDto = tradeService.tradeDecision(baterId, userDetails);
        notificationService.setRead(notificationId);
        return  decisionDto;
    }

    // 평가 페이지 연결
    @GetMapping("/notification/score")
    public OppentScoreResponseDto showOppentScore(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestParam Long notificationId,
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

    // 회원 가입 메시지
    @GetMapping("/notification/signup") // 회원가입 축하 메시지에 대해 이 주소로 요청을 보내면 작동합니다.
    public ResponseEntity<OkDto> signup(@RequestParam Long notificationId){

        notificationService.setRead(notificationId);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}