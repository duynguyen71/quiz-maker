package com.nkd.quizmaker.response;

import com.nkd.quizmaker.model.Assignment;
import lombok.Data;

import java.util.Date;

@Data
public class AssignedDetailsResponse {

    private Date createDate;
    private Date finishDate;
    private int status;
    private int active;
    private QuizInfoResponse quizDetails;

    public AssignedDetailsResponse(Assignment assignment) {
        this.createDate = assignment.getCreatedDate();
        this.finishDate = assignment.getFinishDate();
        this.status = assignment.getStatus();
        this.active = assignment.getStatus();
        this.quizDetails = new QuizInfoResponse(assignment.getQuiz());
    }
}
