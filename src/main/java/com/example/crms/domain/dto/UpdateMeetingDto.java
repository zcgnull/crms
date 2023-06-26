package com.example.crms.domain.dto;


import com.example.crms.domain.vo.MeetingUserVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMeetingDto {
    private int meetingId;
    private int userId;
    private int roomId;
    private String meetingName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp meetingStarttime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp meetingEndtime;
    private String meetingEquipment;
    private String meetingDemand;  //特殊需求
    private String meetingProfile;
    private Map<Integer, Integer> equipments;  //可选设备
    private List<Integer> users;
}
