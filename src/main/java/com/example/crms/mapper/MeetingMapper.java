package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingMapper extends BaseMapper<Meeting> {
}
