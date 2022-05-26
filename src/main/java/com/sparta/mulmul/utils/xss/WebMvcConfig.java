//package com.sparta.mulmul.utils.xss;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebMvcConfig {
//
//    private final ObjectMapper objectMapper;
//
//    @Bean
//    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
//        ObjectMapper copy = objectMapper.copy();
//        copy.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
//        return new MappingJackson2HttpMessageConverter(copy);
//    }
//}