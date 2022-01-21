package com.nkd.quizmaker.response;

import lombok.Data;

import java.util.List;

@Data
public class QuizResponse {

    private long quizId;
    private String title;
    private String code;
    private long limitTime;
    private String subject;
    private int status;
    private int visibility;

    private List<QuestionResponse> questions;

}
