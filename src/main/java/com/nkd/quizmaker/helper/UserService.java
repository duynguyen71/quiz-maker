package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.enumm.ERole;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.*;
import com.nkd.quizmaker.utils.QuizUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final QuizSubmissionRepository submitRepo;
    private final QuizRepository quizRepo;
    private final RoleRepository roleRepo;
    private final AssignmentRepository assignmentRepo;

    public User getCurrentUser() {
        MyUserDetails principal = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }

    public User getByEmail(String email) {
        Optional<User> optional = userRepo.findByEmail(email);
        if (optional.isPresent())
            return optional.get();
        return null;

    }

    public User getByUsername(String username) {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isPresent())
            return optional.get();
        return null;
    }

    public User getByVerificationCode(String code) {
        Optional<User> optional = userRepo.findByVerificationCode(code);
        if (optional.isPresent())
            return optional.get();
        return null;
    }

    public Role getMemberRole() {
        Role role = roleRepo.getByName(ERole.ROLE_MEMBER.name());
        return role;
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public List<QuizSubmission> getCurrentUserSubmissionQuiz(Long quizId) {
        Optional<Quiz> optionalQuiz = quizRepo.findById(quizId);
        if (optionalQuiz.isEmpty()) {
            return null;
        }
        List<QuizSubmission> rs = submitRepo.findAllByQuizAndUser(optionalQuiz.get(), getCurrentUser());
        return rs;
    }


    public List<Quiz> getUserQuizzes(Integer status, Integer active, Pageable pageable) {
        List<Quiz> quizzes = quizRepo.findByUser(getCurrentUser().getUid(), status, active, pageable);
        return quizzes;
    }

    public List<Assignment> getAssignedQuizzes() {
        List<Assignment> assignments = assignmentRepo.findByUserAndActiveAndQuiz_Active(getCurrentUser(), 1, 1);
        return assignments;
    }

    public List<QuizSubmission> getSubmissionQuizzes() {
        List<QuizSubmission> submittedQuizzes = submitRepo.findAllByUser(getCurrentUser());
        return submittedQuizzes;
    }

    public List<User> searchUsers(String username, String email, String fullName, PageRequest pageable) {
        return userRepo.searchUsersNative(username, email, fullName, pageable);
    }

    public String generateCode() {
        String code = QuizUtils.createRandomCode();
        while (userRepo.findByVerificationCode(code).isPresent()) {
            code = QuizUtils.createRandomCode();
        }
        return code;
    }

    boolean existByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    boolean existByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    boolean existByPhone(String phone) {
        return userRepo.existsByPhone(phone);
    }

}
