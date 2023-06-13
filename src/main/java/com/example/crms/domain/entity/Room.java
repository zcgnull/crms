package com.example.crms.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("room")
public class Room {
    @TableId(type = IdType.AUTO)
    private int roomId;
    private String roomName;
    private String roomLocation;
    private int roomCapacity;
    private String roomDescription;
    private int roomIsmutex;
    private int roomState;
}
