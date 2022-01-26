package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.AssignmentInfo;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentInfoRepository extends JpaRepository<AssignmentInfo, Long> {


    Optional<AssignmentInfo> findByQuiz(Quiz quiz);

    List<AssignmentInfo> findAllByQuiz_Owners(User user);

    List<AssignmentInfo> findAllByQuiz_OwnersAndActiveOrderByCreateDateDesc(User owner,Integer active);
}
