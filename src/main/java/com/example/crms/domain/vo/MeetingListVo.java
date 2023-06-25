package com.example.crms.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
public class MeetingListVo {

//    private int meetingId;
    private int roomId;
    private int userId;
    private String roomName;
    private String userName;
    private String meetingName;
    private Timestamp meetingStarttime;
    private Timestamp meetingEndtime;
//    private String meetingDemand;
//    private String meetingProfile;
//    private int meetingState;  //会议状态， 0 未开始   1 已经结束
}
