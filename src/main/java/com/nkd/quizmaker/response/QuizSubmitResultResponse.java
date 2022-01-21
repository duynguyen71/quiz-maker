package com.nkd.quizmaker.response;

import com.nkd.quizmaker.model.QuizSubmission;
import com.nkd.quizmaker.model.SubmissionAnswer;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class QuizSubmitResultResponse {

    private Long submissionId;
    private QuizInfoResponse quizInfo;
    private Date createdDate;
    private Double score;
    private List<AnswerSubmitResultResponse> answers = new ArrayList<>();

    public QuizSubmitResultResponse(QuizSubmission quizSubmission) {
        this.submissionId = quizSubmission.getId();
        this.quizInfo = new QuizInfoResponse(quizSubmission.getQuiz());
        this.createdDate = quizSubmission.getCreateDate();
        this.score = quizSubmission.getScore();
        for (SubmissionAnswer answer :
                quizSubmission.getAnswers()) {
            this.answers.add(new AnswerSubmitResultResponse(answer.getQuestion(), answer.getOption()));
        }
    }


}
