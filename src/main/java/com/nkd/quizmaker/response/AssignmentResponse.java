package com.nkd.quizmaker.response;

import lombok.Data;

import java.util.Date;

@Data
public class AssignmentResponse {

    private Long quizId;
    private String code;
    private String title;
    private Date createdDate;


}
