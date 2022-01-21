package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.model.Quiz;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsById(long questionId);

    @Query(
            nativeQuery = true,
            value = "SELECT q.* FROM question q WHERE (:title IS NULL OR q.title LIKE :title) AND q.active = 1"
    )
    List<Question> getQuestionsNative(String title, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT qs.* FROM question qs JOIN question_quiz qq " +
                    "ON qs.id =qq.question_id " +
                    "JOIN quiz q " +
                    "ON q.id = qq.quiz_id " +
                    "JOIN user_quiz uq " +
                    "ON uq.quiz_id = q.id " +
                    "WHERE uq.user_id = :userId AND qs.id = :questionId "
    )
    Optional<Question> findQuestionNative(Long userId, Long questionId);
}
