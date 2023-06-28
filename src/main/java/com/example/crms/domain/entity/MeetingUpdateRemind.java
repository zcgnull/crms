package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * (MeetingUpdateRemind)表实体类
 *
 * @author makejava
 * @since 2023-06-27 13:00:32
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("meeting_update_remind")
public class MeetingUpdateRemind{

    @TableId
    private Integer id;
    private Integer meetingId;
    private Integer userId;
    private String userName;        //会议预定人姓名
    private String meetingName;   //会议名称
    private Timestamp meetingStarttime;  //会议开始时间
    private Timestamp meetingEndtime;    //会议结束时间

}

