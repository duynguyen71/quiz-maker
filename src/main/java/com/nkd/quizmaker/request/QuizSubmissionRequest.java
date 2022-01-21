package com.nkd.quizmaker.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Data
public class QuizSubmissionRequest {

    private Long quizId;

    private Set<SubmissionAnswerRequest> answers = new LinkedHashSet<>();

    private Date startTime;

    private Date finishTime;
}
