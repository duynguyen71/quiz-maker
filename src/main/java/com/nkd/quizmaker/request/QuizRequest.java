package com.nkd.quizmaker.request;

import com.nkd.quizmaker.enumm.EQuizStatus;
import com.nkd.quizmaker.enumm.EQuizType;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.awt.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequest implements Serializable {

    private Long id;

    @NotBlank
    private String title;


    private String quizImage;

    private Integer limitTime;

    private SubjectRequest subject;

    private List<QuestionRequest> questions;


    private Integer visibility;

    private Date startDate;

    @Enumerated(EnumType.STRING)
    private EQuizType type;

    private Date finishDate;

    private int status =0;


}
