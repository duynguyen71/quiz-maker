package com.nkd.quizmaker.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AssignmentQuizResponse {

    @JsonFormat(pattern = "HH:mm dd-MM")
    private Date startDate;

    @JsonFormat(pattern = "HH:mm dd-MM")
    private Date finishDate;

    @JsonFormat(pattern = "HH:mm dd-MM")
    private Date createDate;

    private Long quizId;

    private String quizTitle;

    private String quizImg;

    private int totalUser;

    private int completed;


    public AssignmentQuizResponse(Date startDate, Date finishDate, Date createDate, Long quizId, String quizTitle, String quizImg, int totalUser, int completed) {
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.createDate = createDate;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.quizImg = quizImg;
        this.totalUser = totalUser;
        this.completed = completed;
    }
}
