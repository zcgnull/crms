package com.example.crms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.vo.DepartmentListVo;
import com.example.crms.mapper.DepartmentMapper;
import com.example.crms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * (Department)表服务实现类
 *
 * @author makejava
 * @since 2023-06-12 14:37:21
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public List<String> getAllDepartmentNames() {

        //查询所有部门信息
        List<Department> departments = departmentMapper.selectList(null);

        //将所有部门信息中的部门名称封装到temp集合中
        List<String> temp = new ArrayList<>();
        for (Department department: departments
        ) {
            temp.add(department.getDepartmentName());
        }

//        DepartmentListVo departmentListVo = new DepartmentListVo();
//
//        //将所有部门名称封装到vo对象中
//        departmentListVo.setDepartmentNames(temp);

        return temp;
    }
}

