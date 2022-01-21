package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.model.Assignment;
import com.nkd.quizmaker.response.AssignedQuizResponse;
import com.nkd.quizmaker.response.QuizOverview;

public class AssignmentMapper {

    public static AssignedQuizResponse assignmentOverviewResponse(Assignment assignment, QuizOverview quizOverview){
        AssignedQuizResponse rs = new AssignedQuizResponse();
        rs.setStartDate(assignment.getStartDate());
        rs.setEndDate(assignment.getFinishDate());
        rs.setStatus(assignment.getStatus());
        rs.setQuizOverview(quizOverview);
        return rs;
    }
}
