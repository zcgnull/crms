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
@TableName("meeting_equipment")
public class MeetingEquipment {

    @TableId(type = IdType.AUTO)
    private int id;
    private int meetingId;
    private int equipmentId;
    private int equipmentNum;

}
