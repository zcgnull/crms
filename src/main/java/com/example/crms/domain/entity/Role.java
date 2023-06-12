package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role")
public class Role {
    private int roleId;
    private String rloeName;
    private int rplePermission;
    private String roleDescription;
}
