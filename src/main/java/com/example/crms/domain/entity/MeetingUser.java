package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("meeting_user")
public class MeetingUser {

    @TableId(type = IdType.AUTO)
    private int id;

    private int meetingId;

    private int userId;

    private int userReply;

    private String userInfo;

    private Timestamp replyTime;

//    private Date replyTime;


    public MeetingUser(int meetingId, int userId){
        this.meetingId = meetingId;
        this.userId = userId;
    }

//    public MeetingUser(int meetingId, int userId, int userReply) {
//        this.meetingId = meetingId;
//        this.userId = userId;
//        this.userReply = userReply;
//    }
}
