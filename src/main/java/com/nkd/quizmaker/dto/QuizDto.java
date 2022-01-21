package com.nkd.quizmaker.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ToString
@Getter
@Setter
public class QuizDto implements Serializable {

    private Long id;

    private String title;

    private String code;

    private String quizImage;

    private SubjectDto subject;

    private Integer limitTime;

    private Date startDate;

    private Integer visibility;

    private Date finishDate;

    private Date createdDate;

    private Date updateDate;


    private List<QuestionDto> questions = new ArrayList<>();

    private int status;


}
