package com.nkd.quizmaker.response;

import com.nkd.quizmaker.dto.OptionDto;
import com.nkd.quizmaker.dto.QuestionDto;

import java.util.List;

public class SubmissionAnswersResponse {

    private Long id;

    private QuestionDto question;

    private List<OptionDto> optionDto;

}
