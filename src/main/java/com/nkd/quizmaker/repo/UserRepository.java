package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Role;
import com.nkd.quizmaker.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String code);


    @Query(nativeQuery = true,
            value = "SELECT u.* FROM user u WHERE (:username IS NULL OR u.username LIKE :username)" +
                    " AND (:email IS NULL OR u.email LIKE :email)" +
                    " AND (:fullName IS NULL OR u.fullName LIKE :fullName)" +
                    " AND u.active = 1")
    List<User> searchUsersNative(String username, String email,String fullName, Pageable pageable);

}
