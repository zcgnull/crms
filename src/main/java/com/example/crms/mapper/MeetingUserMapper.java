package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.MeetingUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeetingUserMapper extends BaseMapper<MeetingUser> {

}
