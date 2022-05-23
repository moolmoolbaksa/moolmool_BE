package com.sparta.mulmul.service;

import com.sparta.mulmul.dto.user.UserCheckResponseDto;
import com.sparta.mulmul.dto.user.UserRequestDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.exception.ErrorCode;
import com.sparta.mulmul.model.Bag;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.BagRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.sparta.mulmul.exception.ErrorCode.*;

// 유저 서비스
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원 정보 초기화 시켜주기
    @Transactional
    public void setUserInfo(UserDetailsImpl userDetails, UserRequestDto requestDto) {

        System.out.println(requestDto.getAddress());
        User user = userRepository.findById(userDetails
                        .getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User's not found error"));

        user.initProfile(requestDto.getAddress());
    }

    // 로그인 체크하기
    public UserCheckResponseDto userCheck(UserDetailsImpl userDetails){

        if ( userDetails.getUserId() == null ) { throw new CustomException(NOT_FOUND_USER); }
        return new UserCheckResponseDto(userRepository
                .findById(userDetails.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER))
        );
    }

}