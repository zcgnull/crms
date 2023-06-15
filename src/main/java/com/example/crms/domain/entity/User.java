package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2023-06-12 15:11:20
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User extends Model<User> {
    //用户id
    @TableId(type = IdType.AUTO)
    private Integer userId;
    
    private String userName;
    
    private String userEmail;
    
    private String userPassword;
    
    private Integer departmentId;

    }

