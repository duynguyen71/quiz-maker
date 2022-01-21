package com.nkd.quizmaker.dto;

import com.nkd.quizmaker.enumm.EAnswerType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ToString
@Getter
@Setter
public class QuestionDto implements Serializable {

    private Long id;

    private String title;

    private Integer position;

    private EAnswerType optionType;

    private List<OptionDto> options =new ArrayList<>();

}
