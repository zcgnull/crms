package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.Meeting;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.vo.MeetingEquipmentVo;
import com.example.crms.domain.vo.MeetingUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {

    List<MeetingEquipmentVo> getMeetingEquipmentByMeetingId(int meetingId);
    List<MeetingUserVo> getMeetingUserByMeetingId(int meetingId);
    List<Meeting> getMettingsByRoomId(int roomId);
    List<Meeting> getAttendMeetingByUserId(int userId, int pageNum, int pageSize);

}
