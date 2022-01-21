package com.nkd.quizmaker.response;

import lombok.Data;

import java.util.Date;
@Data
public class AssignedQuizResponse {


    private Date startDate;

    private Date endDate;

    private QuizOverview quizOverview;

    private Integer status;

}
