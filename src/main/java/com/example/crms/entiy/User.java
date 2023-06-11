package com.example.crms.entiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private int id;
    private String username;
    private String password;

}
