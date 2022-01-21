package com.nkd.quizmaker.response;

import com.nkd.quizmaker.model.Option;
import com.nkd.quizmaker.model.Question;
import lombok.Data;

@Data
public class AnswerSubmitResultResponse {

    private QuestionResultResponse question;
    private OptionResultResponse option;

    public AnswerSubmitResultResponse(Question question, Option option) {
        QuestionResultResponse q = new QuestionResultResponse();
        q.setId(question.getId());
        q.setTitle(question.getTitle());
        this.question = q;
        OptionResultResponse optionResultResponse = new OptionResultResponse();
        optionResultResponse.setId(option.getId());
        optionResultResponse.setContent(option.getContent());
        this.option = optionResultResponse;
    }

    @Data
    static class QuestionResultResponse {
        private Long id;
        private String title;
    }

    @Data
    static class OptionResultResponse {
        private Long id;
        private String content;
    }
}
