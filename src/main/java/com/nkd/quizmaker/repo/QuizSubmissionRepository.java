package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.QuizSubmission;
import com.nkd.quizmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    boolean existsByQuizAndUser(Quiz quiz,User user);

    List<QuizSubmission> findAllByUser(User user);

    List<QuizSubmission> findByQuiz(Quiz quiz);

    List<QuizSubmission> findAllByQuizAndUser(Quiz quiz,User user);

    int countByQuiz(Quiz quiz);

    int countByUserAndQuiz(User user,Quiz quiz);
}
