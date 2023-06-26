package com.example.crms.domain.vo;

import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Meeting;
import com.example.crms.domain.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingVo extends Meeting {

    private String userName; //创建者名字
    private Department department;//创建者部门
    private Room room; //会议室信息
    private List<MeetingEquipmentVo> meetingEquipments = null;
    private List<MeetingUserVo> meetingUsers = null;

}
