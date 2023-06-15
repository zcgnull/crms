package com.example.crms.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomDto {
    private int roomId;
    private String roomName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp roomCreattime;
    private String roomLocation;
    private int roomCapacity;
    private String roomPicurl;
    private String roomDescription;
    private int roomState;
    private List<Integer> departments;
    private Map<Integer, Integer> equipments;
    private List<Integer> fixedRoomIds;
}
