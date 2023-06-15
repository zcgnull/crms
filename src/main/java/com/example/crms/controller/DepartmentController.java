package com.example.crms.controller;


import com.example.crms.domain.ResponseResult;
import com.example.crms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
