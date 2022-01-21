package com.nkd.quizmaker.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nkd.quizmaker.dto.RoleDto;
import com.nkd.quizmaker.dto.UserDto;
import com.nkd.quizmaker.mapper.UserMapper;
import com.nkd.quizmaker.repo.UserRepository;
import com.nkd.quizmaker.utils.MyJWTUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component(value = "authenticate_helper")
@Slf4j
public class AuthenticateHelper {

    private final UserRepository repo;

    public ResponseEntity<?> refreshToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Authorization header is empty!");
        } else {
            String token = authorization.substring("Bearer ".length());
            try {
                DecodedJWT decodedJWT = MyJWTUtils.decodedJWT(token);
                String email = decodedJWT.getSubject();
                UserDto user = UserMapper.toUserDTO(repo.findByEmail(email).get());
                String accessToken = MyJWTUtils.createToken(email, user.getRoles().stream().map(RoleDto::getName).collect(Collectors.toList()));
                String refreshToken = MyJWTUtils.createRefreshToken(email);
                log.info("user " + email + " refresh token success!");
                return ResponseEntity.status(200).body(Map.of("access_token", accessToken, "refresh_token", refreshToken));
            } catch (TokenExpiredException e) {
                log.info("refresh token failed: token expired!");
                TokenExpiredErrorResponse rs = new TokenExpiredErrorResponse();
                rs.setCode(190);
                rs.setMessage("Refresh token expired");
                rs.setType("TokenExpiredException");
                return ResponseEntity.status(401).body(Map.of("error", rs));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Unknown exception!");
            }
        }
    }

    @Data
    public static class TokenExpiredErrorResponse {
        private int code;
        private String message;
        private String type;
    }
}
