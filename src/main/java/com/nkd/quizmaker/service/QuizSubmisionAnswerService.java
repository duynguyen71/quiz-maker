package com.nkd.quizmaker.service;

import com.nkd.quizmaker.model.SubmissionAnswer;
import com.nkd.quizmaker.repo.SubmissionAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizSubmisionAnswerService {

    private final SubmissionAnswerRepository submissionAnswerRepository;

    public SubmissionAnswer save(SubmissionAnswer submissionAnswer){
        return submissionAnswerRepository.save(submissionAnswer);
    }
}
