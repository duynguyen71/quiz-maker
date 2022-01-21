package com.nkd.quizmaker.response;

import lombok.Data;

import java.util.Date;

@Data
public class AssignmentUserResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String avt;

    private boolean isComplete;

    private Double score;

    private Date completeDate;
}
