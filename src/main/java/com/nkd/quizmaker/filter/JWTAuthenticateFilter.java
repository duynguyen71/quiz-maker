package com.nkd.quizmaker.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nkd.quizmaker.model.MyUserDetails;
import com.nkd.quizmaker.service.StreakService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nkd.quizmaker.utils.MyJWTUtils.createRefreshToken;
import static com.nkd.quizmaker.utils.MyJWTUtils.createToken;

@Slf4j
public class JWTAuthenticateFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final StreakService streakService;

    @Autowired
    public JWTAuthenticateFilter(AuthenticationManager authenticationManager, StreakService streakService) {
        this.authenticationManager = authenticationManager;
        this.streakService = streakService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            BufferedReader bufferReader = request.getReader();
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line);
            }
            String parseReq = sb.toString();
            //check body is not null
            if (parseReq != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                AuthRequest authRequest = objectMapper.readValue(parseReq, AuthRequest.class);

                //if username and password not null -> authenticate
                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
                //call user service find user with username
                Authentication authenticate = authenticationManager.authenticate(token);
                //set auth to auth context
                SecurityContextHolder.getContext().setAuthentication(authenticate);
                MyUserDetails principal = (MyUserDetails) authenticate.getPrincipal();
                streakService.save(principal.getUser());
                return authenticate;

            } else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                log.error("Failed to parse authentication request body");
                throw new RuntimeException("Failed to parse authentication request body");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new RuntimeException("Failed to parse authentication request body");
        }
    }

    //success login
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        MyUserDetails user = (MyUserDetails) authResult.getPrincipal();
        //create access token
        String email = user.getUsername();
        List<String> roles = user
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String accessToken = createToken( email, roles);
        //create refresh token
        String refreshToken = createRefreshToken( email);
        //send access token & refresh token to client
        log.info(email + " logged in!");
        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("refreshToken", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(), map);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, String> map = new HashMap<>();
        map.put("error_message", failed.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), map);
    }

    @Data
    static class AuthRequest {
        private String email;
        private String password;
    }
}
