package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepo;

    public Assignment save(Assignment assignment){
        return assignmentRepo.save(assignment);
    }

    public Assignment getByUserAndQuiz(User user,Quiz quiz){
        Optional<Assignment> optional = assignmentRepo.findByUserAndQuiz(user, quiz);
        if(optional.isPresent())
            return optional.get();
        return null;
    }

    public List<Assignment> getAssignmentQuizzes(User user) {
        return assignmentRepo.findByQuizOwnerNative(user.getUid());
    }

    public List<Assignment> getAssignment(Quiz quiz){
        return assignmentRepo.findByQuiz(quiz);
    }


    public int countAssignedUsers(Quiz quiz) {
        return assignmentRepo.countByQuiz(quiz);
    }
}
