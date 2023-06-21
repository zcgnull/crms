package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Equipment;

import java.util.List;

/**
 * (Equipment)表服务接口
 *
 * @author makejava
 * @since 2023-06-21 16:11:49
 */
public interface EquipmentService extends IService<Equipment> {

    ResponseResult pageEquipmentList(Integer pageNum, Integer pageSize, String equipmentName);

    ResponseResult addEquipment(Equipment equipment);

    ResponseResult updateEquipment(Equipment equipment);

    ResponseResult deleteEquipmentIds(List<Integer> equipmentIds);
}

