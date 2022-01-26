package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.model.AssignmentInfo;
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


    List<Assignment> findAllByAssignmentInfo(AssignmentInfo info);

    Optional<Assignment> findByUserAndQuizAndActive(User user, Quiz quiz, Integer active);

    Optional<Assignment> findByIdAndUserAndActive(Assignment.QuizAssignmentId id, User user, Integer active);

    List<Assignment> findAllByUserAndActiveOrderByCreatedDateDesc(User assignedUser, Integer active);

}
