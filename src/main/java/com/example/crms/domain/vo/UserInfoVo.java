package com.example.crms.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data

//使Set方法返回对象，而不再是True和False
@Accessors(chain = true)
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
