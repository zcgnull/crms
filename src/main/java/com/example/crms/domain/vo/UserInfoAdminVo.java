package com.example.crms.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoAdminVo {

    //用户ID
    private Integer userId;
    //用户姓名
    private String userName;
    //用户邮箱
    private String userEmail;
    //用户部门名称
    private String departmentName;
    //用户角色
    private String roleName;

}
