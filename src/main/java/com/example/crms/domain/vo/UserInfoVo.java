package com.example.crms.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
public class UserInfoVo {

    //用户ID
    private Integer userId;
    //用户姓名
    private String userName;
    //用户邮箱
    private String userEmail;
    //用户部门名称
    private String departmentName;
    //所有部门名称
    private List<String> departmentNames;
}
