package com.nkd.quizmaker.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class SubjectRequest {

    private Long id;

    @NotBlank
    @Length(min = 4,max = 30)
    private String title;
}
