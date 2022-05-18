package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.barter.BarterDto;
import com.sparta.mulmul.dto.barter.BarterStatusDto;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.model.Barter;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.BarterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BarterController {
    final private BarterService barterService;
    private final BarterRepository barterRepository;
    private final NotificationRepository notificationRepository;

    /*성훈 - 거래중인 내역보기*/
    @GetMapping("/api/myhistory")
    public List<BarterDto> showMyBarter(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return barterService.showMyBarter(userDetails);
    }

    //엄성훈 - 교환완료 취소
    @PutMapping("/api/myhistory/cancel")
    public ResponseEntity<OkDto> cancelBarter(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        barterService.cancelBarter(barterId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    //엄성훈 - 교환신청 취소
    @DeleteMapping("/api/myhistory")
    public ResponseEntity<OkDto> deleteBarter(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        barterService.deleteBarter(barterId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    //엄성훈 - 교환완료
    @PutMapping("/api/myhistory/handshake")
    public BarterStatusDto OkayItem(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return barterService.OkayBarter(barterId, userDetails);
    }
}
