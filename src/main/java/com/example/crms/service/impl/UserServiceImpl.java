package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.vo.UserInfoVo;
import com.example.crms.mapper.DepartmentMapper;
import com.example.crms.mapper.UserMapper;
import com.example.crms.service.DepartmentService;
import com.example.crms.service.UserService;
import com.example.crms.utils.BeanCopyUtils;
import com.example.crms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-06-12 15:01:42
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private DepartmentMapper departmentMapper;

    //返回用户信息
    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
//        Integer userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
//        User user = getById(userId);
        User user = getById(1);
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        vo.setDepartmentName(departmentMapper.selectById(1).getDepartmentName());
        List<Department> departments = departmentMapper.selectList(null);

        List<String> temp = new ArrayList<>();
        for (Department department: departments
             ) {
            temp.add(department.getDepartmentName());
        }
        vo.setDepartmentNames(temp);
        return ResponseResult.okResult(vo);
    }

    //修改用户信息
    @Override
    public ResponseResult updateUserInfo(UserDto userDto) {

        //根据前端传来的部门名称，得到部门ID
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getDepartmentName,userDto.getDepartmentName());
        Department department = departmentMapper.selectOne(queryWrapper);

        userDto.setDepartmentId(department.getDepartmentId());

        User user = BeanCopyUtils.copyBean(userDto, User.class);

        updateById(user);

        return ResponseResult.okResult();
    }

    //修改密码
    @Override
    public ResponseResult changePassword(String newPassword) {
        //获取当前用户id
//        Integer userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
//        User user = getById(userId);
        User user = getById(1);
        user.setUserPassword(newPassword);

        updateById(user);
        return ResponseResult.okResult();
    }
}

