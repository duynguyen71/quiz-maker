package com.nkd.quizmaker.request;

import lombok.Data;

import java.util.List;

@Data
public class TakeAnswerRequest {

    private Long id;

    private Long questionId;

    private List<OptionRequest> options;

}
