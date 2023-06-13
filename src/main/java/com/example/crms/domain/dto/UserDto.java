package com.example.crms.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer userId;

    private String userName;

//    private String userEmail;

    private String departmentName;

    private Integer departmentId;
}
