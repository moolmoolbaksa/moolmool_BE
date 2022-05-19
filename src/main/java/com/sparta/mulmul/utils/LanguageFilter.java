package com.sparta.mulmul.utils;

import com.sparta.mulmul.dto.chat.MessageRequestDto;
import org.springframework.stereotype.Component;

@Component
public class LanguageFilter {

    private final String[] filterWords = {
            "시발", "씨발","시 발", "씨 발", "씨1발", "시1발", "시벌", "씨벌", "쒸발", "쒸벌", "쒸불",
            "ㅆ1발", "ㅆl발", "ㅅ1발", "ㅅl발", "ㅅㅂ", "ㅆㅂ", "씨팔", "시팔", "개호로", "개같은",
            "개새끼", "ILLHVHL", "ㄱㅐㅅㅐ",
            "새끼",
            "미친놈", "미친년", "미친넘", "미친뇬",
            "병신", "병1신", "병 신", "븅신", "븅쉰",
            "좆", "쥬지", "뷰지", "섹스", "섹1스", "섹 스", "sex", "갈보", "창녀", "빠구리", "빨통", "봊",
            "육변기", "사까시", "사카시", "섹파", "변태", "변녀",
            "느금마", "애미", "느개비", "애비", "앰창", "엠창", "앰생", "엠생",
            "지랄", "존나", "졸라",
            "이기야", "일베충",
            "자살",
            "맘충", "학식충", "급식충", "한남충", "똥꼬충", "롤충", "한녀", "틀딱", "피싸개", "페미충",
            "잼민이", "저능아", "정박아", "찐따", "또라이",
            "fuck", "suck"
    };

    public MessageRequestDto filtering(MessageRequestDto requestDto){

        for (String word : filterWords){
            if (requestDto.getMessage().contains(word)){
                String replace = "";
                for (int i = 0 ; i < word.length(); i++) { replace += "*"; }
                requestDto.setMessage(requestDto.getMessage().replaceAll(word, replace));
            }
        }
        return requestDto;
    }
}
