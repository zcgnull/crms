package com.example.crms.domain.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Schedule)表实体类
 *
 * @author makejava
 * @since 2023-06-18 21:20:10
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("schedule")
public class Schedule {

    @TableId
    private Integer scheduleId;

    private Integer userId;

    private String scheduleName;

//    private Date scheduleStarttime;
    private Timestamp scheduleStarttime;

//    private Date scheduleEndtime;

    private Timestamp scheduleEndtime;

    private Integer meetingId;

    private Integer scheduleStatus;

}

