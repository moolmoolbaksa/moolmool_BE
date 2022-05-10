package com.sparta.mulmul.controller.websocketController;

import com.sparta.mulmul.dto.NotificationDto;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 전체 목록 가져오기
    @GetMapping("/api/notifications")
    public List<NotificationDto> getNotification(@AuthenticationPrincipal UserDetailsImpl userDetails){

        return notificationService.getNotification(userDetails);
    }

    // 알림목록 상세보기
    // 각각 다른 페이지로 연결하게 만들어야 합니다.
    // 알림 메시지로 보내준 글마다 다른 API를 만드는 수 밖에는 없습니다. 여러개의 상세보기 API가 나와야 합니다.

    // 알림 삭제하기
    @DeleteMapping("/api/notification/{notificationId}")
    public ResponseEntity<OkDto> deleteNotification(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @PathVariable Long notificationId){

        notificationService.deleteNotification(notificationId, userDetails);

        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

}
