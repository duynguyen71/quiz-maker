package com.nkd.quizmaker.response;

import com.nkd.quizmaker.model.Quiz;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class QuizInfoResponse {

    private Long quizId;
    private String title;
    private String code;
    private Integer limitTime;
    private int numOfQuestions;
    private String quizImage;
    private Date createdDate;
    private Date startDate;
    private Date finishDate;
    private Integer status;
    private String subject;
    private Integer visibility;

    public QuizInfoResponse(Quiz quiz) {
        this.quizId = quiz.getId();
        this.title = quiz.getTitle();
        this.code = quiz.getCode();
        this.limitTime = quiz.getLimitTime();
        if (quiz.getSubject() != null && quiz.getSubject().getTitle() != null)
            this.subject = quiz.getSubject().getTitle();
        this.numOfQuestions = quiz.getQuestions().size();
        this.quizImage = quiz.getQuizImage();
        this.createdDate = quiz.getCreateDate();
        this.status = quiz.getStatus();
        this.visibility = quiz.getVisibility();
        this.startDate = quiz.getStartDate();
        this.finishDate = quiz.getFinishDate();
    }

}
