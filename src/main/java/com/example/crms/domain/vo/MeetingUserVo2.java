package com.example.crms.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingUserVo2 {
    private int userId;
    private String userName;
    private int userReply;
    private String departmentName;
    private String userInfo;
    private Timestamp replyTime;
}