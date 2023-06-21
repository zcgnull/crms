package com.example.crms.domain.vo;

import com.example.crms.domain.entity.Meeting;
import com.example.crms.domain.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoVo extends Room {

    private int state = 0;  // 默认0  表示可以操作    1 ： 可以预定   2 ：已经被预定  3 ：与其他房间冲突

    private List<Meeting> meetings = new ArrayList<>();  //这个房间的会议
    private List<String> mutexRooms = new ArrayList<>();  //互斥关系的会议室名称
    //private List<Equipment> equments  //设备


}
