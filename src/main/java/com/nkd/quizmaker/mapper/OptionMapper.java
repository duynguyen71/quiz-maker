package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.OptionDto;
import com.nkd.quizmaker.model.Option;
import com.nkd.quizmaker.response.OptionResponse;

public class OptionMapper {

    public static OptionDto toDto(Option option) {
        OptionDto dto = new OptionDto();
        dto.setId(option.getId());
        dto.setContent(option.getContent());
        dto.setScore(option.getScore());
        dto.setTExplanation(option.getTExplanation());
        dto.setFExplanation(option.getFExplanation());
        return dto;
    }

    public static OptionResponse toOptionResponse(Option option){
        OptionResponse optionResponse = new OptionResponse()    ;
        optionResponse.setOptionId(option.getId());
        optionResponse.setContent(option.getContent());
        return optionResponse;
    }
    public static Option toOption(OptionDto dto) {
      Option option = new Option();
      option.setId(dto.getId());
      option.setContent(dto.getContent());
      option.setScore(dto.getScore());
      option.setFExplanation(dto.getFExplanation());
      option.setTExplanation(dto.getTExplanation());
      return option;
    }
}
