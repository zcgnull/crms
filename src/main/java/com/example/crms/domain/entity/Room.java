package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("room")
public class Room {
    @TableId(type = IdType.AUTO)
    private int roomId;
    private String roomName;
    private Timestamp roomCreattime;
    private String roomLocation;
    private int roomCapacity;
    private String roomDescription;
    private String roomPicurl;
    private int roomState;
}
