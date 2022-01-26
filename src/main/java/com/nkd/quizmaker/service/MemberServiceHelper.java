package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.MyUserDetails;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.AssignmentRepository;
import com.nkd.quizmaker.repo.QuizRepository;
import com.nkd.quizmaker.repo.UserRepository;
import com.nkd.quizmaker.response.AssignedDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceHelper {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;


    /**
     * get assigned quiz
     *
     * @param quizId
     */
    public ResponseEntity<?> getAssignedQuiz(long quizId) {
//        User user = userRepository.getById(getCurrentUid());
//        Quiz quiz = quizRepository.getById(quizId);
//        Optional<Assignment> optionalAssignment = assignmentRepository.findByUserAndQuizAndStatusAndActive(user, quiz, 0, 1);
//        if (optionalAssignment.isPresent()) {
//            return ResponseEntity.ok(new AssignedDetailsResponse(optionalAssignment.get()));
//        }
//        return ResponseEntity.badRequest().body("Can not find assigned quiz");
return null;
    }


    /**
     * get all assigned quizzes
     */
    public ResponseEntity<?> getAssignedQuizzes() {
//        User user = userRepository.getById(getCurrentUid());
//        List<Assignment> assignmentList = assignmentRepository.findByUser(user);
//        List<AssignedDetailsResponse> rs = assignmentList.stream().map(assignment -> {
//            AssignedDetailsResponse assignedDetailsResponse = new AssignedDetailsResponse(assignment);
//            return assignedDetailsResponse;
//        }).collect(Collectors.toList());
//
//        return ResponseEntity.ok(rs);
        return null;
    }

    private Long getCurrentUid() {
        MyUserDetails userDetails =
                (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long uid = userDetails.getUser().getUid();
        return uid;
    }
}
