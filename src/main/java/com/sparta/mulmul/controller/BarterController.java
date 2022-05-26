package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.barter.BarterDto;
import com.sparta.mulmul.dto.barter.BarterStatusDto;
import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.dto.barter.BarterTradeCheckDto;
import com.sparta.mulmul.dto.barter.EditRequestDto;
import com.sparta.mulmul.dto.item.ItemRequestDto;
import com.sparta.mulmul.repository.BarterRepository;
import com.sparta.mulmul.repository.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.BarterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BarterTradeCheckDto cancelBarter(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return barterService.cancelBarter(barterId, userDetails);
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
        return barterService.okayBarter(barterId, userDetails);
    }

    //엄성훈 - 교환수정
    @PutMapping("/api/myhistory/edit")
    public ResponseEntity<OkDto> editBarter(@RequestParam Long barterId,
                                            @RequestParam List<Long> itemId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        EditRequestDto editRequestDto = new EditRequestDto(barterId, itemId);
        barterService.editBarter(editRequestDto, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}
