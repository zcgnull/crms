package com.example.crms.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingEquipmentVo{
    private int equipmentId;
    private int equipmentNum;
    private String equipmentName;
}
