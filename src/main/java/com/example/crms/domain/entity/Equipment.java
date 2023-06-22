package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Equipment)表实体类
 *
 * @author makejava
 * @since 2023-06-21 16:11:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("equipment")
public class Equipment{

    @TableId
    private Integer equipmentId;
    
    private String equipmentName;
    
    private Integer equipmentNum;
    
    private Integer equipmentAvailable;

    }

