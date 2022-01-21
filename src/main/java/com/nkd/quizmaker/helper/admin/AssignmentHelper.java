package com.nkd.quizmaker.helper.admin;

import com.nkd.quizmaker.service.AssignmentServiceHelper;
import com.nkd.quizmaker.request.AssignmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssignmentHelper {

    private final AssignmentServiceHelper helper;


    public ResponseEntity<?> addAssignments(AssignmentRequest request) {
        return helper.assignToStudents(request);
    }

    public ResponseEntity<?> getAssignedUserByQuiz(long quizId) {
        return helper.getAssignedUsersByQuiz(quizId);
    }



    public ResponseEntity<?> findAllByUser(long userId) {
        return helper.findAllByUser(userId);
    }
}
