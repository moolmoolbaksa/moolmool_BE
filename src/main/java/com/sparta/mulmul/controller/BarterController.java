package com.sparta.mulmul.controller;

import com.sparta.mulmul.dto.BarterResponseDto;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.service.BarterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BarterController {
    final private BarterService barterService;

    /*성훈 - 거래중인 내역보기*/
    @GetMapping("/api/myhistory")
    public List<BarterResponseDto> showMyBarter (@AuthenticationPrincipal UserDetailsImpl userDetails){
        return barterService.showMyBarter(userDetails);
    }

}
