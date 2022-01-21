package com.nkd.quizmaker.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class SubmissionAnswerRequest {

    private Long questionId;

    private Set<Long> options = new LinkedHashSet<>();

    private String optionText;

}
