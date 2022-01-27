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


    public List<SubmissionAnswer> getSubmitAnswer(QuizSubmission submission, Question question) {
        List<SubmissionAnswer> answers = submissionAnswerRepo.findByQuizSubmissionAndQuestion(submission, question);
        return answers;
    }

    public List<QuizSubmission> getByUserAndSubmitType(User user, Quiz quiz,Integer status, ESubmitType eSubmitType) {
        List<QuizSubmission> rs = quizSubmitRepo.
                findAllByUserAndQuizAndStatusAndSubmissionTypeOrderByCreateDateDesc(user, quiz,status, eSubmitType);
        return rs;
    }

}
