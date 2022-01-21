package com.nkd.quizmaker.mapper;

import com.nkd.quizmaker.dto.UserDto;
import com.nkd.quizmaker.model.User;
import com.nkd.quizmaker.response.UserOverviewResponse;
import lombok.Data;

import java.util.stream.Collectors;

@Data
public class UserMapper {


    public static UserDto toUserDTO(User user) {
        UserDto dto = new UserDto();
        dto.setUid(user.getUid());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setAvtImgUrl(user.getAvtImgUrl());
        dto.setCoverImgUrl(user.getCoverImgUrl());

        dto.setVerificationCode(user.getVerificationCode());


        dto.setRoles(
                user.getRoles().stream()
                        .map(RoleMapper::toRoleDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public static UserOverviewResponse toUserOverviewResponse(User user){
        UserOverviewResponse rsp = new UserOverviewResponse();
        rsp.setId(user.getUid());
        rsp.setUsername(user.getUsername());
        rsp.setFullName(user.getFullName());
        rsp.setEmail(user.getEmail());
        return rsp;
    }
}
