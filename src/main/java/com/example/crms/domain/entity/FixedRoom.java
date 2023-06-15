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
@TableName("fixed_room")
public class FixedRoom {

    @TableId(type = IdType.AUTO)
    private int fixedRoomId;
    private String fixedRoomName;
    private String fixedRoomLocation;
    private String fixedRoomDescription;
    private int fixedRoomState;

}
