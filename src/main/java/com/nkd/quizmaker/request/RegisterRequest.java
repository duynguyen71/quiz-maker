package com.nkd.quizmaker.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @NotBlank
    private Integer age;

    @NotBlank
    private Integer gender;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
