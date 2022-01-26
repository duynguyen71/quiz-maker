package com.nkd.quizmaker.service;

import com.nkd.quizmaker.enumm.ESubmitType;
import com.nkd.quizmaker.model.*;
import com.nkd.quizmaker.repo.QuizRepository;
import com.nkd.quizmaker.repo.QuizSubmissionRepository;
import com.nkd.quizmaker.repo.SubmissionAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmitRepo;
    private final SubmissionAnswerRepository submissionAnswerRepo;
    private final QuizRepository quizRepo;

    public QuizSubmission save(QuizSubmission quizSubmission) {
        return quizSubmitRepo.save(quizSubmission);
    }

    public List<QuizSubmission> getUserSubmissionQuiz(User user, long quizId) {
        Optional<Quiz> optionalQuiz = quizRepo.findById(quizId);
        if (optionalQuiz.isEmpty()) {
            return null;
        }
        List<QuizSubmission> rs = quizSubmitRepo.findAllByQuizAndUserOrderByAttemptDesc(optionalQuiz.get(), user);
        return rs;
    }


    public List<QuizSubmission> getSubmissions(User user) {
        return quizSubmitRepo.findAllByUser(user);
    }

    public int countUserSubmittedQuiz(User user, Quiz quiz) {
        return quizSubmitRepo.countByUserAndQuiz(user, quiz);
    }

    public int countSubmitted(Quiz quiz) {
        int i = quizSubmitRepo.countByQuiz(quiz);
        return i;
    }

    public List<SubmissionAnswer> getSubmitAnswer(QuizSubmission submission, Question question) {
        List<SubmissionAnswer> answers = submissionAnswerRepo.findByQuizSubmissionAndQuestion(submission, question);
        return answers;
    }

    public List<QuizSubmission> getByUserAndSubmitType(User user, Quiz quiz, ESubmitType eSubmitType) {
        List<QuizSubmission> rs = quizSubmitRepo.
                findAllByUserAndQuizAndSubmissionTypeOrderByCreateDateDesc(user, quiz, eSubmitType);
        return rs;
    }

}
