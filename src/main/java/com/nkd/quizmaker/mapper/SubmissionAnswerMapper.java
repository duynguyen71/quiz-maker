package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.QuestionDto;
import com.nkd.quizmaker.dto.SubmissionAnswerDto;
import com.nkd.quizmaker.model.Question;
import com.nkd.quizmaker.model.SubmissionAnswer;
import com.nkd.quizmaker.response.SubmissionAnswersResponse;

public class SubmissionAnswerMapper {

    public static SubmissionAnswerDto toDto(SubmissionAnswer submissionAnswer) {
        SubmissionAnswerDto dto = new SubmissionAnswerDto();
        dto.setOptionDto(OptionMapper.toDto(submissionAnswer.getOption()));
        Question question = submissionAnswer.getQuestion();
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(question.getId());
        questionDto.setTitle(question.getTitle());
        dto.setQuestion(questionDto);
        dto.setId(submissionAnswer.getId());
        return dto;

    }


}
