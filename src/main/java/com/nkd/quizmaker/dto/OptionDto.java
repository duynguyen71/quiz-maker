package com.nkd.quizmaker.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;


@ToString
@Getter
@Setter
public class OptionDto implements Serializable {


    private Long id;

    private String content;

    private Double score;

    private String type;

    private String tExplanation;

    private String fExplanation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionDto)) return false;
        OptionDto optionDto = (OptionDto) o;
        return Objects.equals(getId(), optionDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
