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
    private String roomName;  //名称
    private Timestamp roomCreattime; //创建时间
    private String roomLocation;  //位置
    private String roomEquipment;  //固定设备
    private int roomCapacity;  //最大人数
    private String roomDescription; //会议室描述
    private String roomPicurl; //图片地址
    private int roomState;   //状态
}
