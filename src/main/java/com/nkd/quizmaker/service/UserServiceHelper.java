package com.nkd.quizmaker.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nkd.quizmaker.dto.RoleDto;
import com.nkd.quizmaker.dto.UserDto;
import com.nkd.quizmaker.enumm.ERole;
import com.nkd.quizmaker.mapper.UserMapper;
import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.MyUserDetails;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.AssignmentRepository;
import com.nkd.quizmaker.repo.RoleRepository;
import com.nkd.quizmaker.repo.UserRepository;
import com.nkd.quizmaker.response.AssignmentResponse;
import com.nkd.quizmaker.utils.MyJWTUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("UserServiceHelper")
@RequiredArgsConstructor
@Slf4j
public class UserServiceHelper {


    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AssignmentRepository assRepo;

    public ResponseEntity<?> getUserDetails() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDto user =UserMapper.toUserDTO( userDetails.getUser());
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> refreshToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Authorization header is empty!");
        } else {
            String token = authorization.substring("Bearer ".length());
            try {
                DecodedJWT decodedJWT = MyJWTUtils.decodedJWT(token);
                String email = decodedJWT.getSubject();
                UserDto user = UserMapper.toUserDTO(userRepo.findByEmail(email).get());
                String accessToken = MyJWTUtils.createToken(email, user.getRoles().stream().map(RoleDto::getName).collect(Collectors.toList()));
                String refreshToken = MyJWTUtils.createRefreshToken(email);
                log.info("user " + email + " refresh token success!");
                return ResponseEntity.status(200).body(Map.of("access_token", accessToken, "refresh_token", refreshToken));
            } catch (TokenExpiredException e) {
                log.info("refresh token failed: token expired!");
                AuthenticateHelper.TokenExpiredErrorResponse rs = new AuthenticateHelper.TokenExpiredErrorResponse();
                rs.setCode(190);
                rs.setMessage("Refresh token expired");
                rs.setType("TokenExpiredException");
                return ResponseEntity.status(403).body(Map.of("error", rs));
            } catch (Exception e) {
                e.printStackTrace();
//token format exc
                return ResponseEntity.status(403).body(e.getMessage());
            }
        }
    }

    public ResponseEntity<?> register(User user) {
//        if (user == null || user.getUsername() == null || user.getPassword() == null || user.getEmail() == null)
//            return ResponseEntity.badRequest().body("Bad request");
        if (userRepo.existsByEmail(user.getEmail()))
            return ResponseEntity.badRequest().body("Email has been used");
        //set role
        if (user.getRoles() != null
                && user.getRoles().size() > 0
                && roleRepo.existsById(user.getRoles().get(0).getId())) {
            user.setRoles(Collections.singletonList(roleRepo.getById(user.getRoles().get(0).getId())));
        } else {
            user.setRoles(Collections.singletonList(roleRepo.getByName(ERole.ROLE_MEMBER.name())));
        }
        //
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserDto dto = UserMapper.toUserDTO(userRepo.save(user));
        List<String> rls = dto.getRoles().stream().map(roleDto -> roleDto.getName()).collect(Collectors.toList());
        String accessToken = MyJWTUtils.createToken(dto.getEmail(), rls);
        String refreshToken = MyJWTUtils.createRefreshToken(dto.getEmail());
        return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken));
    }

    @Data
    public static class TokenExpiredErrorResponse {
        private int code;
        private String message;
        private String type;
    }

}
