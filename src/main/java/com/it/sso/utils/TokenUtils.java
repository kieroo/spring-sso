package com.it.sso.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class TokenUtils {
    private static final long TOKEN_EXPIRE_TIME = 1800 * 1000;
    private static final String ISSUER = "ssoIt";

    public static String generateToken(String claimName, String value, String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + TOKEN_EXPIRE_TIME);
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(now)
                .withExpiresAt(expireTime)
                .withClaim(claimName, value)
                .sign(algorithm);
        return token;
    }

    public static boolean verifyToken(String token, String secretKey) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier jwtVerifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUserInfo(String token, String infoName) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String info = decodedJWT.getClaim(infoName).asString();
        return info;
    }

    public static void main(String[] args) {

        long begin = System.currentTimeMillis();
        boolean b = TokenUtils.verifyToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzc29JdCIsImV4cCI6MTU5MTAxODk5NiwiaWF0IjoxNTkxMDE3MTk2LCJ1c2VybmFtZSI6InhpYW9ob25nIn0.ODcK7lxbIvTlis43BeMaOhUmEX8ekFErYjOCEy1NGDI", "123456");
        long end = System.currentTimeMillis();
        System.out.println("b = " + b);
        System.out.println(end-begin);
    }
}
