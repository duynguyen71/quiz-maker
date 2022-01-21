package com.nkd.quizmaker.response;

import com.nkd.quizmaker.model.QuizSubmission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class QuizSubmitResponseDetail {

    private long id;
    private Date createdDate;
    private UserInfo userInfo;
    private List<Question> answers = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UserInfo {
        private String username;
        private String email;
    }

    public QuizSubmitResponseDetail(QuizSubmission quizSubmission) {
        this.id = quizSubmission.getId();
        this.createdDate = quizSubmission.getCreateDate();
        this.userInfo = new UserInfo(quizSubmission.getUser().getUsername(), quizSubmission.getUser().getEmail());
        quizSubmission.getAnswers().forEach(submissionAnswer -> {
            Question question = new Question();
            question.setId(submissionAnswer.getQuestion().getId());
            question.setTitle(submissionAnswer.getQuestion().getTitle());
            question.setOption(new Option(submissionAnswer.getOption().getId(), submissionAnswer.getOption().getContent(), submissionAnswer.getOption().getScore()));
            this.answers.add(question);
        });
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    static class Question {
        private long id;
        private String title;
        private Option option;
    }

    @AllArgsConstructor
    @Data
    static class Option {
        private long id;
        private String content;
        private double score;
    }

}
