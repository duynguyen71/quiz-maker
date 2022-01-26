package com.nkd.quizmaker.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class AssignmentRequest {

    private Long id;

    @Min(0)
    @NotNull
    private Long quizId;

    @NotNull
    @NotEmpty
    private Set<String> emails;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Date startDate;

    @NotNull
    private Date finishDate;

    private Integer status = 1;

    private Integer active =1;
}
