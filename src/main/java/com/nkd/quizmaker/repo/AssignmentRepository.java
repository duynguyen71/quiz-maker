package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Assignment.QuizAssignmentId> {


    List<Assignment> findByUser(User user);

    List<Assignment> findByQuiz(Quiz quiz);

    Optional<Assignment> findByUserAndQuiz(User user, Quiz quiz);

    Optional<Assignment> findByUserAndQuizAndStatusAndActive(User user, Quiz quiz, int status, int active);

    List<Assignment> findByUserAndActiveAndQuiz_Active(User user, int active, int quizStatus);

    int countByQuiz(Quiz quiz);

    List<Assignment> findByQuiz_Users(User user);

    List<Assignment> findByUser_Quizzes(Quiz quiz);

    @Query(
            nativeQuery = true,
            value = "SELECT a.* FROM user u JOIN user_quiz uq ON u.uid = uq.user_id " +
                    "JOIN assignment a ON uq.quiz_id = a.quiz_id " +
                    "WHERE u.uid = :ownerId "
    )
    List<Assignment> findByQuizOwnerNative(@NotNull Long ownerId);

}
