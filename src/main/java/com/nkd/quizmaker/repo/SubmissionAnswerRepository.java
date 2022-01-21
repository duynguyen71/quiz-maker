package com.nkd.quizmaker.repo;

import com.nkd.quizmaker.model.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long> {

    /**
     * @param submissionId
     * @param questionId
     * @return list of option ids
     */
    @Query(value = "SELECT * FROM submission_answer WHERE submission_id =?1 AND question_id =?2", nativeQuery = true)
    List<Long> findOptionIds(long submissionId, long questionId);
}
