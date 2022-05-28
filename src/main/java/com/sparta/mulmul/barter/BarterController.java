package com.sparta.mulmul.barter;

import com.sparta.mulmul.dto.OkDto;
import com.sparta.mulmul.barter.barterDto.BarterDto;
import com.sparta.mulmul.barter.barterDto.BarterStatusDto;
import com.sparta.mulmul.barter.barterDto.BarterTradeCheckDto;
import com.sparta.mulmul.barter.barterDto.EditRequestDto;
import com.sparta.mulmul.websocket.NotificationRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
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
    @GetMapping("/user/barters")
    public List<BarterDto> showMyBarter(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return barterService.showMyBarter(userDetails);
    }

    //엄성훈 - 교환완료 취소
    @PutMapping("/user/barters/cancel")
    public BarterTradeCheckDto cancelBarter(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return barterService.cancelBarter(barterId, userDetails);
    }

    //엄성훈 - 교환신청 취소
    @DeleteMapping("/user/barters")
    public ResponseEntity<OkDto> deleteBarter(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        barterService.deleteBarter(barterId, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }

    //엄성훈 - 교환완료
    @PutMapping("/user/barters/handshake")
    public BarterStatusDto OkayItem(@RequestParam Long barterId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return barterService.okayBarter(barterId, userDetails);
    }

    //엄성훈 - 교환수정
    @PutMapping("/user/barters/edit")
    public ResponseEntity<OkDto> editBarter(@RequestParam Long barterId,
                                            @RequestParam List<Long> itemId,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        EditRequestDto editRequestDto = new EditRequestDto(barterId, itemId);
        barterService.editBarter(editRequestDto, userDetails);
        return ResponseEntity.ok().body(OkDto.valueOf("true"));
    }
}
