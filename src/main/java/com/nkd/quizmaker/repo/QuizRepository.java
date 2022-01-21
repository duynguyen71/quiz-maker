package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByCode(String code);

    Optional<Quiz> findByCodeAndActive(String code, int active);

    boolean existsByCode(String code);

    List<Quiz> findTop10ByOrderByCreateDateAsc();

    @Query(nativeQuery = true,
            value = "SELECT * FROM quiz q JOIN user_quiz uq ON q.id = uq.quiz_id WHERE uq.user_id=:userId " +
                    "AND (:status IS NULL OR q.status = :status) " +
                    "AND (:active IS NULL OR q.active = :active)")
    List<Quiz> findByUser(Long userId, Integer status, Integer active, Pageable pageable);


    @Query(nativeQuery = true,
            value = "SELECT exists (SELECT * FROM user_quiz WHERE user_id=?1 AND quiz_id=?2)")
    int existsByUserIdAndQuizId(long uid, long quizId);

    @Query(
            nativeQuery = true,
            value = "SELECT q.* FROM quiz q LEFT JOIN subject s ON q.subject_id = s.id " +
                    "WHERE (:title IS NULL OR q.title LIKE :title) " +
                    "AND (:subject IS NULL OR s.title LIKE :subject) " +
                    "AND (:active IS NULL OR q.active = :active) " +
                    "AND (:status IS NULL OR q.status = :status)"
    )
    List<Quiz> getQuizzesNative(String subject, String title, Integer active, Integer status, Pageable pageable);


    Optional<Quiz> findByIdAndOwners(Long id, User user);

    Optional<Quiz> findByIdAndOwnersAndQuestions_Active(Long id, User user, Integer active);
}
