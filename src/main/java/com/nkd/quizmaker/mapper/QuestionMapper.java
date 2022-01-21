package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.QuestionDto;
import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.response.QuestionResponse;

import java.util.stream.Collectors;

public class QuestionMapper {


    public static QuestionDto toDto(com.nkd.quizmaker.model.Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setPosition(question.getPosition());
        dto.setOptionType(question.getOptionType());
        //set options
        dto.setOptions(
                question.getOptions().stream()
                        .map(OptionMapper::toDto).collect(Collectors.toList())
        );
        return dto;
    }

    public static QuestionResponse toQuestionResponse(Question question) {
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setQuestionId(question.getId());
        questionResponse.setTitle(question.getTitle());
        questionResponse.setOptionType(question.getOptionType());
        questionResponse.setOptions(
                question.getOptions().stream()
                        .map(OptionMapper::toOptionResponse).collect(Collectors.toList())
        );
        return questionResponse;
    }

    public static Question toQuestion(QuestionDto dto) {
        Question question = new Question();
        question.setId(dto.getId());
        question.setTitle(dto.getTitle());
        question.setOptionType(dto.getOptionType());

        question.setOptions(
                dto.getOptions().stream().map(OptionMapper::toOption)
                        .collect(Collectors.toList())
        );
        return question;
    }
}
