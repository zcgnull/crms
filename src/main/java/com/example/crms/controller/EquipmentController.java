package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Equipment;
import com.example.crms.domain.entity.Role;
import com.example.crms.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    /**
     *  分页查询设备列表
     * @param pageNum
     * @param pageSize
     * @param equipmentName
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getEquipmentList(Integer pageNum, Integer pageSize, String equipmentName){
        return equipmentService.pageEquipmentList(pageNum,pageSize,equipmentName);
    }


    /**
     *  新增设备
     * @param equipment
     * @return
     */
    @PostMapping("/addEquipment")
    public ResponseResult addEquipment(@RequestBody Equipment equipment){
        return equipmentService.addEquipment(equipment);

    }

    /**
     * 修改设备信息
     */
    @PutMapping
    public ResponseResult edit(@RequestBody Equipment equipment)
    {

        return equipmentService.updateEquipment(equipment);
    }

    /**
     * 删除可选设备
     * @param equipmentIds
     */
    @DeleteMapping("/equipmentIds")
    public ResponseResult remove(@RequestParam List<Integer> equipmentIds) {

        return equipmentService.deleteEquipmentIds(equipmentIds);
    }
}
