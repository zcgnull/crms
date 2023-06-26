package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("meeting")
public class Meeting {

    @TableId(type = IdType.AUTO)
    private int meetingId;  //会议id
    private int roomId;     //会议室id
    private int userId;    //预定人id
    private String meetingName;   //会议名称
    private Timestamp meetingStarttime;  //会议开始时间
    private Timestamp meetingEndtime;    //会议结束时间
    private String meetingEquipment;   //手写的可选设备需求
    private String meetingDemand;   //会议特殊需求
    private String meetingProfile;  //会议简介
    private int meetingState;  //会议状态， 0 未开始   1 已经结束

}
