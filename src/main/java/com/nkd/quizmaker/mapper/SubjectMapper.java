package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.SubjectDto;
import com.nkd.quizmaker.model.Subject;

public class SubjectMapper {

    public static SubjectDto toDto(Subject subject) {
            SubjectDto dto = new SubjectDto();
            dto.setId(subject.getId());
            dto.setTitle(subject.getTitle());
            return dto;

    }
}
