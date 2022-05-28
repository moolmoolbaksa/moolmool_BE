package com.sparta.mulmul.utils;

import com.sparta.mulmul.websocket.chatDto.MessageRequestDto;
import com.sparta.mulmul.websocket.ChatFilter;
import com.sparta.mulmul.websocket.chat.ChatFilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageFilter {

    private final ChatFilterRepository filterRepository;
    private String[] filterWords;

    @PostConstruct
    private void init(){

        List<ChatFilter> filters = filterRepository.findAll();
        filterWords = new String[filters.size()];

        for ( int i = 0 ; i < filters.size() ; i++ ){
            filterWords[i] = filters.get(i).getFilter();
        }

    }

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
