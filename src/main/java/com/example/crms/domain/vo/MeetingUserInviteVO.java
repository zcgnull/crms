package com.example.crms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingUserInviteVO {
    private int id;
    private int meetingId;
    private String meetingName;
    //参会人ID
    private int userId;
    //会议预定人名称
    private String userName;
    private int userReply;
    private String userInfo;
    private Timestamp replyTime;

    private int roomId;
    private String roomName;

    private Timestamp meetingStarttime;  //会议开始时间
    private Timestamp meetingEndtime;    //会议结束时间

    private int meetingState;  //会议状态， 0 未开始   1 已经结束
}
