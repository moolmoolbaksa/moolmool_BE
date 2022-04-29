package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.UserCheckResponseDto;
import com.sparta.mulmul.dto.UserRequestDto;
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

// 유저 서비스
@Service
@RequiredArgsConstructor
public class UserService {



    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BagRepository bagRepository;


    // 회원가입 처리
    public void signup(UserRequestDto requestDto){

        // 회원가입 유효성 검사 실시 (혹시 valid check를 시행할 하나의 공통 메소드를 만들 방법을 연구해 보도록 합니다.)
        checkBy("usernameAndNickname", requestDto);
        // 비밀번호 암호화
        String EncodedPassword = passwordEncoder.encode(requestDto.getPassword());
        // 회원가입 및 반환


        bagRepository.save(new Bag(userRepository.save(
                User.withPassword(requestDto, EncodedPassword)
        ))
        );

    }

    // 아이디 중복 체크
    public void checkBy(String userInfo, UserRequestDto requestDto){

        if (userInfo.equals("username")) { // username에 대한 중복 체크 시행
            if ( userRepository
                    .findByUsername(requestDto.getUsername())
                    .isPresent() )
            { throw new IllegalArgumentException("이메일이 중복됩니다.");}
        } else if (userInfo.equals("nickname")) { // nickname에 대한 중복 체크 시행
            if ( userRepository
                    .findByNickname(requestDto.getNickname())
                    .isPresent() )
            { throw new IllegalArgumentException("닉네임이 중복됩니다.");}
        } else if (userInfo.equals("usernameAndNickname")) {
            if ( userRepository
                    .findByUsername(requestDto.getUsername())
                    .isPresent() )
            { throw new IllegalArgumentException("이메일이 중복됩니다.");}
            if ( userRepository
                    .findByNickname(requestDto.getNickname())
                    .isPresent() )
            { throw new IllegalArgumentException("닉네임이 중복됩니다.");}
        }
        else { // 메소드 인자 입력 오류
            throw new IllegalArgumentException("\"username\", \"nickname\", \"usernameAndNickname\"을 인자로 중복체크를 시행해 주세요.");
        }
    }

    // 회원 정보 초기화 시켜주기
    @Transactional
    public void setUserInfo(UserDetailsImpl userDetails, UserRequestDto requestDto) {

        User user = userRepository.findById(userDetails
                .getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User's not found error"));

        user.initProfile(requestDto);
    }

    // 로그인 체크하기
   public UserCheckResponseDto userCheck(UserDetailsImpl userDetails){

        if ( userDetails.getUserId() == null ) { throw new NullPointerException("User's not found"); }
        return new UserCheckResponseDto(userRepository
                .findById(userDetails.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User's not found error"))
        );
    }

}