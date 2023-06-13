package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.vo.PageVo;
import com.example.crms.domain.vo.UserInfoVo;
import com.example.crms.domain.vo.UserVo;
import com.example.crms.mapper.DepartmentMapper;
import com.example.crms.mapper.UserMapper;
import com.example.crms.service.DepartmentService;
import com.example.crms.service.UserService;
import com.example.crms.utils.BeanCopyUtils;
import com.example.crms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    //查询用户信息
    @Override
    public ResponseResult selectUserPage(UserDto userDto, Integer pageNum, Integer pageSize) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();

        //姓名模糊查询
        queryWrapper.like(StringUtils.hasText(userDto.getUserName()),User::getUserName,userDto.getUserName());

        Integer departmentIdTemp = -1;
        if (userDto.getDepartmentName() != null) {

            LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            departmentLambdaQueryWrapper.eq(Department::getDepartmentName,userDto.getDepartmentName());
            Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
            departmentIdTemp = department.getDepartmentId();
        }

        //部门查询
        queryWrapper.eq(StringUtils.hasText(userDto.getDepartmentName()),User::getDepartmentId,departmentIdTemp);

        Page<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        List<User> users = page.getRecords();

        List<UserVo> userVoList = users.stream()
                .map(u -> BeanCopyUtils.copyBean(u, UserVo.class))
                .collect(Collectors.toList());

        for (UserVo userVo: userVoList
             ) {
            Department department = departmentMapper.selectById(userVo.getDepartmentId());
            userVo.setDepartmentName(department.getDepartmentName());
        }

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(userVoList);

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public boolean checkEmailUnique(UserAddDto userAddDto) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUserEmail,userAddDto.getUserEmail()))==0;
    }

    @Override
    public ResponseResult addUser(UserAddDto userAddDto) {
//        //密码加密处理
//        user.setPassword(passwordEncoder.encode(user.getPassword()));

        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        departmentLambdaQueryWrapper.eq(Department::getDepartmentName,userAddDto.getDepartmentName());
        Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
        userAddDto.setDepartmentId(department.getDepartmentId());
        User user = BeanCopyUtils.copyBean(userAddDto, User.class);
        save(user);

//        if(user.getRoleIds()!=null&&user.getRoleIds().length>0){
//            insertUserRole(user);
//        }
        return ResponseResult.okResult();
    }
}

