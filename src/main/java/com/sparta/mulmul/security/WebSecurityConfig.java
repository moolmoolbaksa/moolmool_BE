package com.sparta.mulmul.security;

import com.sparta.mulmul.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

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
                .csrf()
                .disable();

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
        restLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
        restLoginFilter.afterPropertiesSet();
        return restLoginFilter;
    }

    @Bean
    public RestLoginSuccessHandler formLoginSuccessHandler() {
        return new RestLoginSuccessHandler();
    }

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
        skipPathList.add("POST,/user/signup");
        skipPathList.add("POST,/user/login");
        skipPathList.add("POST,/user/id-check");
        skipPathList.add("POST,/user/nickname-check");

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

}
