package com.sparta.mulmul.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sparta.mulmul.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public final class JwtTokenUtils {

    @Value("${jwt.secret-key}")
    private static String jwtSecret;

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 액세스 토큰의 유효기간: 30분 (단위: seconds)
    private static final int ACCESS_TOKEN_VALID_SEC = 7 * DAY;
    // JWT 액세스 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int ACCESS_TOKEN_VALID_MILLI_SEC = ACCESS_TOKEN_VALID_SEC * 1000;

    private static final int REFRESH_TOKEN_VALID_SEC = 7 * DAY;
    private static final int REFRESH_TOKEN_VALID_MILLI_SEC = REFRESH_TOKEN_VALID_SEC * 1000;

    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
    public static final String CLAIM_USER_ID = "USER_ID";
    public static final String CLAIM_NICK_NAME = "NICK_NAME";
    public static final String JWT_SECRET = jwtSecret;

    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    public static String generateJwtToken(UserDetailsImpl userDetails, String tokenType) {
        String token = null;
        int validMilliSec;

        switch (tokenType){
            case ACCESS_TOKEN: validMilliSec = ACCESS_TOKEN_VALID_MILLI_SEC; break;
            case REFRESH_TOKEN: validMilliSec = REFRESH_TOKEN_VALID_MILLI_SEC; break;
            default: throw new IllegalArgumentException("JwtTokenUtils: 인자 값이 잘못되었습니다. (ACCESS_TOKEN or REFRESH_TOKEN)");
        }

        try {
            token = JWT.create()
                    .withIssuer("moolmool")
                    .withClaim(CLAIM_USER_ID, userDetails.getUserId())
                    .withClaim(CLAIM_NICK_NAME, userDetails.getNickname())
                     // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + validMilliSec))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}
