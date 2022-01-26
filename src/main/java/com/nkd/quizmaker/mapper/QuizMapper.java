package com.nkd.quizmaker.mapper;


import com.nkd.quizmaker.dto.QuizDto;
import com.nkd.quizmaker.model.Quiz;
import com.nkd.quizmaker.model.QuizSubmission;
import com.nkd.quizmaker.model.Subject;
import com.nkd.quizmaker.response.QuizOverview;
import com.nkd.quizmaker.response.QuizResponse;
import com.nkd.quizmaker.response.QuizSubmissionResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuizMapper {

    public static QuizResponse toQuizResponse(Quiz quiz) {
        QuizResponse resp = new QuizResponse();
        resp.setQuizId(quiz.getId());
        resp.setTitle(quiz.getTitle());
        resp.setVisibility(quiz.getVisibility());
        resp.setStatus(quiz.getStatus());

        resp.setCode(quiz.getCode()
        );
        resp.setSubject(quiz.getSubject().getTitle());

        resp.setQuestions(
                quiz.getQuestions().stream()
                        .map(QuestionMapper::toQuestionResponse)
                        .collect(Collectors.toList())
        );
//        resp.setLimitTime(quiz.getLimitTime());
        return resp;
    }

    public static QuizDto toDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setCode(quiz.getCode());
        dto.setQuizImage(quiz.getQuizImage());
        dto.setCreatedDate(quiz.getCreateDate());
        dto.setUpdateDate(quiz.getUpdateDate());
        dto.setLimitTime(quiz.getLimitTime());
        dto.setStartDate(quiz.getStartDate());
        dto.setVisibility(quiz.getVisibility());
        dto.setFinishDate(quiz.getFinishDate());
        dto.setStatus(quiz.getStatus());
        if (quiz.getSubject() != null) {
            dto.setSubject(SubjectMapper.toDto(quiz.getSubject()));
        }
        //set questions
        dto.setQuestions(
                quiz.getQuestions().stream().map(QuestionMapper::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }


    public static QuizOverview toQuizOverview(
            Quiz quiz, Integer playedCount
    ) {
        QuizOverview quizOverview = new QuizOverview();
        quizOverview.setId(quiz.getId());
        quizOverview.setTitle(quiz.getTitle());
        quizOverview.setLimitTime(quiz.getLimitTime());
        quizOverview.setNumOfQuestions(quiz.getQuestions().size());
        quizOverview.setStatus(quiz.getStatus());
        quizOverview.setActive(quiz.getActive());
        quizOverview.setCreateDate(quiz.getCreateDate());
        quizOverview.setImage(quiz.getQuizImage());
        quizOverview.setPlayedCount(playedCount);
        quizOverview.setCode(quiz.getCode());
        return quizOverview;
    }
    public static QuizOverview toQuizOverview(
            Quiz quiz, double score
    ) {
        QuizOverview quizOverview = new QuizOverview();
        quizOverview.setId(quiz.getId());
        quizOverview.setTitle(quiz.getTitle());
        quizOverview.setLimitTime(quiz.getLimitTime());
        quizOverview.setNumOfQuestions(quiz.getQuestions().size());
        quizOverview.setStatus(quiz.getStatus());
        quizOverview.setActive(quiz.getActive());
        quizOverview.setCreateDate(quiz.getCreateDate());
        quizOverview.setImage(quiz.getQuizImage());
        quizOverview.setScore(score);
        quizOverview.setCode(quiz.getCode());
        return quizOverview;
    }




    public static QuizSubmissionResponse toQuizSubmissionResponse(QuizSubmission quizSubmission) {
        QuizSubmissionResponse resp = new QuizSubmissionResponse();
        resp.setId(quizSubmission.getId());
        resp.setQuizTitle(quizSubmission.getQuiz().getTitle());
        return resp;
    }

    public static Map<String, Object> toQuizDetailsResponse(Quiz quiz) {
        Map<String, Object> rs = new HashMap<>();
        rs.put("id", quiz.getId());
        rs.put("title", quiz.getTitle());
        rs.put("createDate", quiz.getCreateDate());
        rs.put("modifyDate", quiz.getUpdateDate());
        rs.put("status", quiz.getStatus());
        rs.put("code", quiz.getCode());
        rs.put("limitTime", quiz.getLimitTime());
        rs.put("type", quiz.getQuizType() != null ? quiz.getQuizType().name() : "None");
        rs.put("numOfQuestions", quiz.getQuestions().size());
        Subject subject = quiz.getSubject();
        rs.put("subject", Map.of(
                "id", subject.getId(),
                "title", subject.getTitle()
        ));
        List<Map<String, Object>> questions = quiz.getQuestions().stream()
                .map(question -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", question.getId());
                    map.put("optionType", question.getOptionType());
                    map.put("title", question.getTitle());
                    map.put("numOfOption", question.getOptions().size());
                    List<Map<String, Object>> options = question.getOptions().stream()
                            .map(option -> {
                                Map<String, Object> o = new HashMap<>();
                                o.put("id", option.getId());
                                o.put("content", option.getContent());
                                o.put("tExplanation", option.getTExplanation());
                                o.put("fExplanation", option.getFExplanation());
                                o.put("score", option.getScore());
                                return o;
                            })
                            .collect(Collectors.toList());
                    map.put("options", options);
                    return map;
                }).collect(Collectors.toList());
        rs.put("questions", questions);
        return rs;
    }

}
