package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Room;
import com.example.crms.domain.vo.RoomIdsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {

    List<Department> getDepartmentsByRoomId(int roomId);
    List<RoomIdsVo> getRoomIdsVoList();
}
