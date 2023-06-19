package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 * (Schedule)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-18 21:20:09
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    List<Schedule> selectExistStatus(Integer userId, Date scheduleStarttime, Date scheduleEndtime);

    List<Schedule> selectUserStatus(Date scheduleStarttime, Date scheduleEndtime);
}

