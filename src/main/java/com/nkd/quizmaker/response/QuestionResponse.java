package com.nkd.quizmaker.response;

import com.nkd.quizmaker.enumm.EAnswerType;
import com.nkd.quizmaker.request.OptionRequest;
import lombok.Data;

import java.util.List;
@Data
public class QuestionResponse {

    private Long questionId;

    private String title;

    private EAnswerType optionType;

    private List<OptionResponse> options;
}
