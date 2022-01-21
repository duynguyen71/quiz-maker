package com.nkd.quizmaker.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class AssignmentRequest {

    @Min(0)
    @NotNull
    private Long quizId;

    @NotNull
    @NotEmpty
    private Set<String> emails;

    @NotNull
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm")
    private Date startDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm")
    private Date finishDate;
}
