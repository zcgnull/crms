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
    private int meetingId;
    private int roomId;
    private int userId;
    private String meetingName;
    private Timestamp meetingStarttime;
    private Timestamp meetingEndtime;
    private String meetingEquipment;
    private String meetingDemand;
    private String meetingProfile;
    private int meetingState;  //会议状态， 0 未开始   1 已经结束

}
