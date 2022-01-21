package com.nkd.quizmaker.request;

import com.nkd.quizmaker.enumm.EAnswerType;
import lombok.Data;

import java.util.List;
@Data
public class QuestionRequest {

    private Long id;

    private String title;

    private EAnswerType optionType;

    private List<OptionRequest> options;
}
