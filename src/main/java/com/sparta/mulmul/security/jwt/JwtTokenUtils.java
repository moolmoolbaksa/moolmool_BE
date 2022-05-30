package com.sparta.mulmul.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sparta.mulmul.security.UserDetailsImpl;
import com.sparta.mulmul.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.sparta.mulmul.security.RestLoginSuccessHandler.TOKEN_TYPE;

@Component
public final class JwtTokenUtils {

    public static String JWT_SECRET;

    @Value("${jwt.secret-key}")
    private void setKey(String secret) {
        JwtTokenUtils.JWT_SECRET = secret;
    }

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 액세스 토큰의 유효기간: 30분 (단위: seconds)
    private static final int JWT_TOKEN_VALID_SEC = 3 * MINUTE;
    // JWT 액세스 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000;

    // JWT 액세스 토큰의 유효기간: 30분 (단위: seconds)
    private static final int REFRESH_TOKEN_VALID_SEC = 3 * DAY;
    // JWT 액세스 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int REFRESH_TOKEN_VALID_MILLI_SEC = REFRESH_TOKEN_VALID_SEC * 1000;

    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
    public static final String CLAIM_USER_ID = "USER_ID";
    public static final String CLAIM_NICK_NAME = "NICK_NAME";

    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public static String generateAccessToken(UserDetailsImpl userDetails) {

        String token = null;

        try {
            token = JWT.create()
                    .withIssuer("moolmool")
                    .withClaim(CLAIM_USER_ID, userDetails.getUserId())
                    .withClaim(CLAIM_NICK_NAME, userDetails.getNickname())
                     // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    public static String generateRefreshToken(UserDetailsImpl userDetails) {

        String token = null;

        try {
            token = JWT.create()
                    .withIssuer("moolmool")
                    .withClaim(CLAIM_USER_ID, userDetails.getUserId())
                    .withClaim(CLAIM_NICK_NAME, userDetails.getNickname())
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    public String getJwtToken(User user, String type){
        switch (type){
            case REFRESH_TOKEN:
                return TOKEN_TYPE + " " + generateAccessToken(
                        UserDetailsImpl.fromUser(user)
                );
            case ACCESS_TOKEN:
                return TOKEN_TYPE + " " + generateRefreshToken(
                        UserDetailsImpl.fromUser(user)
                );
            default: throw new IllegalArgumentException("토큰 타입을 정확히 기입해 주세요.( REFRESH_TOKEN / ACCESS_TOKEN )");
        }
    }

    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}
