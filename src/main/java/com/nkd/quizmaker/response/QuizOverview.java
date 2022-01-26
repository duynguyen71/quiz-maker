package com.nkd.quizmaker.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QuizOverview {

    private Long id;

    private String code;

    private String title;

    private int numOfQuestions;

    private Integer limitTime;

    private int status;

    private int active;

    private String image;

    private Date createDate;

    private Integer playedCount;

    private double score;

    private List<QuestionResponse> questions;


}
