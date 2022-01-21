package com.nkd.quizmaker.dto;

import lombok.Data;

@Data
public class SubmissionAnswerDto {

    private Long id;

    private QuestionDto question;

    private OptionDto optionDto;
}
