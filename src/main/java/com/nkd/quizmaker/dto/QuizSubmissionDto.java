package com.nkd.quizmaker.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class QuizSubmissionDto {

    private Long id;

    private QuizDto quiz;

    private Double score;

    private List<SubmissionAnswerDto> answers = new ArrayList<>();
}
