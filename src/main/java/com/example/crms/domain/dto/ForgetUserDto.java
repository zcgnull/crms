package com.example.crms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetUserDto {
    private String userEmail;
    private String code;
    private String userPassword;
    private String confirmPassword;

}
