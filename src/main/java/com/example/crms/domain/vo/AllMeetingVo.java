package com.example.crms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllMeetingVo {


    private String meetingName;
    private Timestamp meetingStarttime;
    private Timestamp meetingEndtime;
    private int meetingState;  //会议状态， 0 未开始   1 已经结束
}
