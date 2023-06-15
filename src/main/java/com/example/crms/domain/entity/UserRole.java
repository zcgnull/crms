package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (UserRole)表实体类
 *
 * @author makejava
 * @since 2023-06-13 16:54:20
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_role")
public class UserRole{

    @TableId
    private Integer id;

    private Integer userId;

    private Integer roleId;

    }

