package com.example.crms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.PrinterGraphics;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMeetingDto {

    private int roomId;
    private String meetingName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp meetingStarttime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp meetingEndtime;
    private String meetingEquipment;   //手写的可选设备需求
    private String meetingDemand;  //特殊需求
    private String meetingProfile;
    private Map<Integer, Integer> equipments;  //可选设备
    private List<Integer> users;

}
