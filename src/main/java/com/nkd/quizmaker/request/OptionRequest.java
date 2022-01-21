package com.nkd.quizmaker.request;

import lombok.Data;

@Data
public class OptionRequest {

    private Long id;

    private String content;

    private Double score;

    private String tExplanation;

    private String fExplanation;
}
