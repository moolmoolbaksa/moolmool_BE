package com.sparta.mulmul.config;

import com.sparta.mulmul.security.FilterSkipMatcher;
import com.sparta.mulmul.security.RestFailureHandler;
import com.sparta.mulmul.security.RestLoginSuccessHandler;
import com.sparta.mulmul.security.filter.JwtAuthFilter;
import com.sparta.mulmul.security.filter.RestLoginFilter;
import com.sparta.mulmul.security.jwt.HeaderTokenExtractor;
import com.sparta.mulmul.security.provider.JWTAuthProvider;
import com.sparta.mulmul.security.provider.RestLoginAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.AUTH_HEADER;
import static com.sparta.mulmul.security.RestLoginSuccessHandler.REFRESH_HEADER;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    public WebSecurityConfig(
            JWTAuthProvider jwtAuthProvider,
            HeaderTokenExtractor headerTokenExtractor
    ) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.headerTokenExtractor = headerTokenExtractor;
    }

    @Bean
    public BCryptPasswordEncoder encodePassword(){ return new BCryptPasswordEncoder(); }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(restLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web){
        // 접근 완전 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // POST 요청에 대한 CSRF를 추가로 무시해 줘야 접근이 가능합니다.
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin();

        // cors 필터 등록
        http
                .cors()
                .configurationSource(corsConfigurationSource());

        // 세션 종료
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 만든 것으로는 작동하지 않는다. Filter를 등록해야지 사용이 가능하다.
        http
                .addFilterBefore(restLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .anyRequest()
                .permitAll();

    }

    @Bean
    public RestLoginFilter restLoginFilter() throws Exception {
        RestLoginFilter restLoginFilter = new RestLoginFilter(authenticationManager());
        restLoginFilter.setFilterProcessesUrl("/user/login");
        restLoginFilter.setAuthenticationFailureHandler(restFailureHandler());
        restLoginFilter.setAuthenticationSuccessHandler(restLoginSuccessHandler());
        restLoginFilter.afterPropertiesSet();
        return restLoginFilter;
    }

    @Bean
    public RestLoginSuccessHandler restLoginSuccessHandler() {
        return new RestLoginSuccessHandler();
    }

    @Bean
    public RestFailureHandler restFailureHandler() { return new RestFailureHandler(); }

    @Bean
    public RestLoginAuthProvider restLoginAuthProvider() {
        return new RestLoginAuthProvider(encodePassword());
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        // h2-console 허용
        skipPathList.add("GET,/h2-console/**");
        skipPathList.add("POST,/h2-console/**");
        // 회원 관리 API 허용
        skipPathList.add("POST,/user/id-check");
        skipPathList.add("POST,/user/nickname-check");
        skipPathList.add("GET,/user/kakao");
        skipPathList.add("GET,/user/naver");

        skipPathList.add("GET,/favicon.ico");

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(
                matcher,
                headerTokenExtractor
        );
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//https://main.d38cmg5gt99sfb.amplifyapp.com
        configuration.addAllowedOrigin("https://moolmooldoctor.firebaseapp.com");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://kapi.kakao.com/v2/user/me");
        configuration.addAllowedOrigin("https://kapi.kakao.com/v2/user/me");
        configuration.addAllowedOrigin("http://openapi.naver.com/v1/nid/me");
        configuration.addAllowedOrigin("https://openapi.naver.com/v1/nid/me");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader(AUTH_HEADER);
        configuration.addExposedHeader(REFRESH_HEADER);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
