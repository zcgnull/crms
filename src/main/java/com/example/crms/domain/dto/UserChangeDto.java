package com.example.crms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChangeDto {

    private Integer userId;

    private String userName;

    private String userEmail;

//    private String userPassword;

    private String departmentName;

    private String roleName;
}
