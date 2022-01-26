package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.model.QuizSubmission;
import com.nkd.quizmaker.model.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long> {

    List<SubmissionAnswer> findByQuizSubmissionAndQuestion(QuizSubmission submission, Question question);

    List<SubmissionAnswer> findByQuizSubmissionOrderByCreateDateDesc(QuizSubmission quizSubmission);
}
