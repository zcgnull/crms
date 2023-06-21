package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Equipment;
import com.example.crms.domain.entity.Role;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.domain.vo.PageVo;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.mapper.EquipmentMapper;
import com.example.crms.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * (Equipment)表服务实现类
 *
 * @author makejava
 * @since 2023-06-21 16:11:49
 */
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public ResponseResult pageEquipmentList(Integer pageNum, Integer pageSize, String equipmentName) {

        LambdaQueryWrapper<Equipment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(equipmentName), Equipment::getEquipmentName, equipmentName);

        Page page = new Page(pageNum, pageSize);
        Page page1 = page(page, queryWrapper);


        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(page1.getRecords());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addEquipment(Equipment equipment) {

        //判断该设备名称是否已经存在
        LambdaQueryWrapper<Equipment> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.eq(Equipment::getEquipmentName, equipment.getEquipmentName());

        if (equipmentMapper.selectOne(LambdaQueryWrapper) != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.EquipmentName_EXIST);
        }

        equipment.setEquipmentAvailable(equipment.getEquipmentNum());

        save(equipment);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateEquipment(Equipment equipment) {

        if (equipment.getEquipmentNum() < 1) {
            return ResponseResult.errorResult(508, "可选设备数量必须大于0");
        }

        String equipmentName = equipment.getEquipmentName();

        //没有修改角色名，只修改了其它信息
        Equipment equipment1 = equipmentMapper.selectById(equipment.getEquipmentId());

        Integer equipmentAvailable = equipment1.getEquipmentAvailable();
        Integer equipmentNum = equipment1.getEquipmentNum();

        Integer occupyNum = equipmentNum - equipmentAvailable;

        if (equipment.getEquipmentNum() < occupyNum) {
            return ResponseResult.errorResult(508, "可选设备数量小于正在使用中的设备数量");
        }

        if (equipment1.getEquipmentName().equals(equipmentName)) {
            equipment.setEquipmentAvailable(equipment.getEquipmentNum() - occupyNum);
            updateById(equipment);
            return ResponseResult.okResult();
        }

        //判断该角色名称是否已经存在
        LambdaQueryWrapper<Equipment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Equipment::getEquipmentName, equipment.getEquipmentName());

        if (equipmentMapper.selectOne(lambdaQueryWrapper) != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.EquipmentName_EXIST);
        }
        equipment.setEquipmentAvailable(equipment.getEquipmentNum() - occupyNum);
        updateById(equipment);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteEquipmentIds(List<Integer> equipmentIds) {
        int i = 0;
        for(;i<equipmentIds.size();i++) {
            Equipment equipment = equipmentMapper.selectById(equipmentIds.get(i));

            if (equipment.getEquipmentNum() - equipment.getEquipmentAvailable() > 0) {
                return ResponseResult.errorResult(AppHttpCodeEnum.EquipmentOcc_EXIST);
            }
        }
        removeByIds(equipmentIds);
        return ResponseResult.okResult();
    }

}

