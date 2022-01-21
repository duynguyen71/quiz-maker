package com.nkd.quizmaker.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public class MyJWTUtils {

    private static final String secret = "afhasflkhskuridfbiabribr";

    public static String createRefreshToken(String email) {
        Date expiresAt = new Date(System.currentTimeMillis() + 10 * 60 * 1000 * 60 * 10);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String refresh_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(expiresAt)
                .withIssuer("khanduy@gmail.com")
                .sign(algorithm);
        return refresh_token;
    }

    public static String createToken(String email, List<String> roles) {
        Date expiresAt = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * 60 * 10);
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        String access_token = JWT.create()
                .withSubject(email)
                .withExpiresAt(expiresAt)
                .withIssuer("khanduy@gmail.com")
                .withClaim("roles", roles)
                .sign(algorithm);
        return access_token;
    }

    public static DecodedJWT decodedJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT verify = verifier.verify(token);
        return verify;
    }
}
