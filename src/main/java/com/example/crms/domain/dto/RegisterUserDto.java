package com.example.crms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {
    private String userName;
    private String userEmail;
    private String code;
    private String userPassword;
    private String confirmPassword;
    private String departmentName;
}
