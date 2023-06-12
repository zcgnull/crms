package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Department)表实体类
 *
 * @author makejava
 * @since 2023-06-12 14:32:51
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("department")
public class Department{
    @TableId
    private Integer departmentId;
    
    private String departmentName;

    }

