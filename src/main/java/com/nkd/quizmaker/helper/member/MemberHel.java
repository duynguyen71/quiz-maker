package com.nkd.quizmaker.helper.member;

import com.nkd.quizmaker.service.MemberQuizSubmissionHelper;
import com.nkd.quizmaker.service.MemberServiceHelper;
import com.nkd.quizmaker.request.QuizSubmissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberHel {

    private final MemberServiceHelper memberServiceHelper;
    private final MemberQuizSubmissionHelper memberQuizSubmissionHelper;

    public ResponseEntity<?> submitAnswers(QuizSubmissionRequest quizSubmissionRequest) {
        return memberQuizSubmissionHelper.submitAnswers(quizSubmissionRequest);
    }

    public ResponseEntity<?> getSubmittedQuizzes() {
        return memberQuizSubmissionHelper.getAllSubmittedQuizzes();
    }

    public ResponseEntity<?> getAssignedQuizzes() {
        return memberServiceHelper.getAssignedQuizzes();
    }

    public ResponseEntity<?> getAssignedQuiz(long quizId) {
        return memberServiceHelper.getAssignedQuiz(quizId);

    }

}
