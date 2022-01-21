package com.nkd.quizmaker.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TakeRequest {

    private Long id;

    private Long quizId;

    private List<TakeAnswerRequest> answers = new ArrayList<>();

    private String description;


}
