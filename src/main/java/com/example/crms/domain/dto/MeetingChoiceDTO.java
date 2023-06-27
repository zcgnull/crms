package com.example.crms.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingChoiceDTO {

    private int id;
    private int userId;
    private int userReply;
    private int meetingId;
    private String userInfo;
    private Timestamp replyTime;
//    private Date replyTime;
}
