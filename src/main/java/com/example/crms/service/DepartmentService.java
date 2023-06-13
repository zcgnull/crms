package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Department;

import java.util.List;

/**
 * (Department)表服务接口
 *
 * @author makejava
 * @since 2023-06-12 14:37:21
 */
public interface DepartmentService extends IService<Department> {

    List<String> getAllDepartmentNames();
}

