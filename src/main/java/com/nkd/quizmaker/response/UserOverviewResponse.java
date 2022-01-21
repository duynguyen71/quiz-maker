package com.nkd.quizmaker.response;

import lombok.Data;

@Data
public class UserOverviewResponse {

    private Long id;

    private String email;

    private String username;

    private String fullName;

    private String avt;
}
