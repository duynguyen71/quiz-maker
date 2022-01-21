package com.nkd.quizmaker.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 6, max = 30)
    private String fullName;

    @NotBlank
    @Length(min = 6, max = 15)
    private String username;

    @NotBlank
    private String password;

    private Integer gender = 2;
}
