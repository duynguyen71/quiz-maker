package com.nkd.quizmaker.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nkd.quizmaker.helper.MyUserDetailHel;
import com.nkd.quizmaker.model.MyUserDetails;
import com.nkd.quizmaker.service.StreakService;
import com.nkd.quizmaker.utils.MyJWTUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MyAuthorizationFilter extends OncePerRequestFilter {

    public static final String AUTH_URL_PATTERN = "\\.*/auth/\\.*";
    public static final String PUBLIC_URL_PATTERN = "\\.*/public\\.*";

    private final MyUserDetailHel userDetailsService;
    private final StreakService streakService;

    @Autowired
    public MyAuthorizationFilter(MyUserDetailHel userDetailsService, StreakService streakService) {
        this.userDetailsService = userDetailsService;
        this.streakService = streakService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //get req url
        String requestUrl = request.getRequestURI();

        Pattern publicUrlPtn = Pattern.compile(PUBLIC_URL_PATTERN);
        Pattern authUrlPattern = Pattern.compile(AUTH_URL_PATTERN);

        //check url
        if (authUrlPattern.matcher(requestUrl).find() || publicUrlPtn.matcher(requestUrl).find()) {
            SecurityContextHolder.getContext().setAuthentication(null);
            filterChain.doFilter(request, response);
        } else {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String token = header.substring("Bearer ".length());
                    //try to decode token
                    DecodedJWT decodedJWT = MyJWTUtils.decodedJWT(token);
                    String email = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

                    List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UserDetails principal = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);

                    MyUserDetails u =(MyUserDetails) authenticationToken.getPrincipal();
                    streakService.save(u.getUser());
                    filterChain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    TokenExpiredErrorResponse resp = new TokenExpiredErrorResponse();
                    resp.setCode(190);
                    resp.setType("TokenExpiredException");
                    resp.setMessage("access token expired...");
                    Map<String, TokenExpiredErrorResponse> error = Map.of("error", resp);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                } catch (Exception e) {
                    filterChain.doFilter(request, response);

                }
            } else {
                filterChain.doFilter(request, response);
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
