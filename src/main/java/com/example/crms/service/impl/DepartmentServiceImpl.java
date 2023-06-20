package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Role;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.domain.vo.DepartmentListVo;
import com.example.crms.domain.vo.PageVo;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.mapper.DepartmentMapper;
import com.example.crms.mapper.UserMapper;
import com.example.crms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public ResponseResult getdepartmentList(Integer pageNum, Integer pageSize, String departmentName) {
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(departmentName), Department::getDepartmentName, departmentName);

        Page page = new Page(pageNum, pageSize);
        Page page1 = page(page, queryWrapper);


        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(page1.getRecords());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addDepartment(Department department) {

        //判断该部门名称是否已经存在
        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        departmentLambdaQueryWrapper.eq(Department::getDepartmentName, department.getDepartmentName());

        if (departmentMapper.selectOne(departmentLambdaQueryWrapper) != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DepartmentName_EXIST);
        }

        save(department);
        return ResponseResult.okResult();
    }

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResponseResult deleteDepartmentById(List<Integer> departmentIds) {

        //判断该部门是否存在员工，如果存在员工，则不能删除
        int i = 0;
        for(;i<departmentIds.size();i++) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getDepartmentId, departmentIds.get(i));

            List<User> users = userMapper.selectList(userLambdaQueryWrapper);
            if (users.size() != 0) {
                return ResponseResult.errorResult(AppHttpCodeEnum.UserDepartment_EXIST);
            }
        }

        removeByIds(departmentIds);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateDepartment(Department department) {

        //判断该部门名称是否已经存在
        LambdaQueryWrapper<Department> deLambdaQueryWrapper = new LambdaQueryWrapper<>();
        deLambdaQueryWrapper.eq(Department::getDepartmentName, department.getDepartmentName());

        if (departmentMapper.selectOne(deLambdaQueryWrapper) != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DepartmentName_EXIST);
        }
        updateById(department);
        return ResponseResult.okResult();
    }
}

