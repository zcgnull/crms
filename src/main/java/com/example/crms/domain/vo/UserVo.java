package com.example.crms.domain.vo;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.crms.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {

    //用户ID
    private Integer userId;

    private String userName;

    private String userEmail;

    private String userPassword;

    private Integer departmentId;
    //用户部门名称
    private String departmentName;


}
