package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.QuizSubmission;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.repo.QuizRepository;
import com.nkd.quizmaker.repo.QuizSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmitRepo;
    private final QuizRepository quizRepo;

    public QuizSubmission save(QuizSubmission quizSubmission) {
        return quizSubmitRepo.save(quizSubmission);
    }

    public List<QuizSubmission> getUserSubmissionQuiz(User user, long quizId) {
        Optional<Quiz> optionalQuiz = quizRepo.findById(quizId);
        if (optionalQuiz.isEmpty()) {
            return null;
        }
        List<QuizSubmission> rs = quizSubmitRepo.findAllByQuizAndUser(optionalQuiz.get(), user);
        return rs;
    }

    public List<QuizSubmission> getSubmissions(User user) {
        return quizSubmitRepo.findAllByUser(user);
    }

    public int countUserSubmittedQuiz(User user, Quiz quiz) {
        return quizSubmitRepo.countByUserAndQuiz(user, quiz);
    }

}
