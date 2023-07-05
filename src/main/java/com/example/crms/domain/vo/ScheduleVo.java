package com.example.crms.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVo {

    private Integer scheduleId;

    private Integer userId;
    private String departmentName;
    private String userName;

    private String scheduleName;

//    private Date scheduleStarttime;
    private Timestamp scheduleStarttime;

//    private Date scheduleEndtime;

    private Timestamp scheduleEndtime;

//    private Integer meetingId;

    private Integer scheduleStatus;
    private String Status;

}

