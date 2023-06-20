package com.example.crms.controller;


import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Role;
import com.example.crms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //所有部门名称信息
    @GetMapping("/allDepartmentNames")
    public List<String> departmentInfo(){

        return departmentService.getAllDepartmentNames();
    }

    /**
     *  分页查询部门列表
     * @param pageNum
     * @param pageSize
     * @param departmentName
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getdepartmentList(Integer pageNum, Integer pageSize, String departmentName){
        return departmentService.getdepartmentList(pageNum,pageSize,departmentName);
    }


    /**
     *  添加部门信息
     * @param department
     * @return
     */
    @PostMapping("/addDepartment")
    public ResponseResult addDepartment(@RequestBody Department department){
        return departmentService.addDepartment(department);

    }

    /**
     * 修改部门信息
     */
    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody Department department)
    {
        return departmentService.updateDepartment(department);
    }

    /**
     * 删除部门
     * @param departmentIds
     */
    @DeleteMapping("/departmentIds")
    public ResponseResult remove(@RequestParam List<Integer> departmentIds) {

        return departmentService.deleteDepartmentById(departmentIds);
    }
}
