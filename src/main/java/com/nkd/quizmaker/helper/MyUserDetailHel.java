package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.mapper.UserMapper;
import com.nkd.quizmaker.model.MyUserDetails;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailHel implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepo.findByEmail(s);
        if (optionalUser.isEmpty()) {
            log.error(String.format("User: %s not found", s));
            throw new UsernameNotFoundException(String.format("User: %s not found", s));
        }
        return new MyUserDetails(optionalUser.get());
    }
}
