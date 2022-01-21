package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.QuizSubmissionDto;
import com.nkd.quizmaker.model.QuizSubmission;
import com.nkd.quizmaker.model.SubmissionAnswer;

public class QuizSubmissionMapper {

    public static QuizSubmissionDto toDto(QuizSubmission quizSubmission) {
        QuizSubmissionDto dto = new QuizSubmissionDto();
        dto.setId(quizSubmission.getId());
        dto.setScore(quizSubmission.getScore());
        for (SubmissionAnswer answer :
                quizSubmission.getAnswers()) {
            dto.getAnswers().add(
                    SubmissionAnswerMapper.toDto(answer)
            );
        }
        return dto;
    }
}
