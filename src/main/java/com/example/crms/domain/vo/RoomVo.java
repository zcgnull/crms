package com.example.crms.domain.vo;

import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.FixedRoom;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomVo {

    private int roomId;
    private String roomName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp roomCreattime;
    private String roomLocation;
    private int roomCapacity;
    private String roomPicurl;
    private String roomDescription;
    private int roomState;
    private List<Department> departments;
    private List<FixedRoom> fixedRooms;

}
