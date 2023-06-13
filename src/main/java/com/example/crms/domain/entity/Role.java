package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Role)表实体类
 *
 * @author makejava
 * @since 2023-06-13 16:45:11
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("role")
public class Role{
    @TableId
    private Integer roleId;
    
    private String roleName;
    
    private Integer rolePermission;
    
    private String roleDescription;

    }

