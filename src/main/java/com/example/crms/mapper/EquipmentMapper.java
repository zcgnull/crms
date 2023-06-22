package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Equipment)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-21 16:11:43
 */
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

}

