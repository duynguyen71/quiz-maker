package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.AssignmentInfo;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.AssignmentInfoRepository;
import com.nkd.quizmaker.repo.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepo;
    private final AssignmentInfoRepository assignmentInfoRepo;

    public Assignment save(Assignment assignment) {
        return assignmentRepo.save(assignment);
    }

    public Assignment getByUserAndQuiz(User user, Quiz quiz, Integer active) {

        return assignmentRepo.findByUserAndQuizAndActive(user, quiz, active).orElse(null);
    }

    public Assignment getAssignmentByIdAndAssignedUser(Assignment.QuizAssignmentId id, User user) {
        return assignmentRepo.findByIdAndUserAndActive(id, user, 1).orElse(null);
    }


    public AssignmentInfo saveInfo(AssignmentInfo assignmentInfo) {
        return assignmentInfoRepo.save(assignmentInfo);
    }

    public List<Assignment> getAssignmentByInfo(AssignmentInfo info) {
        return assignmentRepo.findAllByAssignmentInfo(info);
    }


    public AssignmentInfo getAssignmentInfos(Long id) {
        return assignmentInfoRepo.findById(id).orElse(null);
    }


    public List<Assignment> getAssignedQuizzes(User user, Integer active) {
        return assignmentRepo.findAllByUserAndActiveOrderByCreatedDateDesc(user, active);
    }

    public List<AssignmentInfo> getAssignmentInfos(User owner, Integer active) {
        return assignmentInfoRepo.findAllByQuiz_OwnersAndActiveOrderByCreateDateDesc(owner, active);
    }


}
