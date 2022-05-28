package com.sparta.mulmul.user;

import com.sparta.mulmul.user.userDto.UserCheckResponseDto;
import com.sparta.mulmul.user.userDto.UserRequestDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

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