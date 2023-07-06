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
@TableName("meeting_delete_remind")
public class MeetingDeleteRemind {
    @TableId
    private Integer id;
//    private Integer meetingId;
    private Integer userId;
    private String userName;        //会议预定人姓名
    private String meetingName;   //会议名称
    private Timestamp meetingStarttime;  //会议开始时间
    private Timestamp meetingEndtime;    //会议结束时间

}
