package com.nkd.quizmaker.controller;

import com.nkd.quizmaker.helper.UserHelper;
import com.nkd.quizmaker.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserHelper userHelper;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid RegistrationRequest req) {
        return userHelper.registration(req);
    }

    @GetMapping("/verification")
    public ResponseEntity<?> verification(@RequestHeader("code") String code) {
        return userHelper.verification(code);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("refresh-token") String token) {
        return userHelper.refreshToken(token);
    }



}
