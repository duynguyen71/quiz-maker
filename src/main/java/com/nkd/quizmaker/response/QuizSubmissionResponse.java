package com.nkd.quizmaker.response;

import lombok.Data;

import java.util.Date;
@Data
public class QuizSubmissionResponse {

    private Long id;

    private Double score;

    private Date submitDate;

    private Long quizId;

    private String quizTitle;
}
