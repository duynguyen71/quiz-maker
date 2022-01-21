package com.nkd.quizmaker.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString
public class UserDto {

    private Long uid;
    private String username;
    private String password;
    private String email;
    private String avtImgUrl;
    private String coverImgUrl;
    private List<RoleDto> roles;

    private String verificationCode;
    private int isEnable;
}
