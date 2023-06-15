package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserChangeDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Role;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.domain.vo.PageVo;
import com.example.crms.domain.vo.UserInfoAdminVo;
import com.example.crms.domain.vo.UserInfoVo;
import com.example.crms.domain.vo.UserVo;
import com.example.crms.mapper.DepartmentMapper;
import com.example.crms.mapper.RoleMapper;
import com.example.crms.mapper.UserMapper;
import com.example.crms.mapper.UserRoleMapper;
import com.example.crms.service.DepartmentService;
import com.example.crms.service.UserService;
import com.example.crms.utils.BeanCopyUtils;
import com.example.crms.utils.SecurityUtils;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private DepartmentService departmentService;

    //返回用户信息
    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
//        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
        Integer userId = 24;
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        //通过部门Id,得到用户部门名称,并封装到vo对象中
        vo.setDepartmentName(departmentMapper.selectById(user.getDepartmentId()).getDepartmentName());

        //查询所有部门信息
        List<String> allDepartmentNames = departmentService.getAllDepartmentNames();

//        List<Department> departments = departmentMapper.selectList(null);
//
//        //将所有部门信息中的部门名称封装到temp集合中
//        List<String> temp = new ArrayList<>();
//        for (Department department: departments
//             ) {
//            temp.add(department.getDepartmentName());
//        }

        //将所有部门名称封装到vo对象中
        vo.setDepartmentNames(allDepartmentNames);


        //通过用户id，从user_role表中找到roleId
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId,vo.getUserId());
        Integer roleId = userRoleMapper.selectOne(queryWrapper).getRoleId();

        //通过roleId,得到roleName
        vo.setRoleName(roleMapper.selectById(roleId).getRoleName());


        return ResponseResult.okResult(vo);
    }

    //修改用户信息
    @Override
    public ResponseResult updateUserInfo(UserDto userDto) {

        //获取当前用户id
//        Integer userId = SecurityUtils.getUserId();
        //测试使用，先将用户Id设置为24
        Integer userId = 24;

        userDto.setUserId(userId);
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
        //测试使用，先将用户Id设置为24
        Integer userId = 24;
        //根据用户id查询用户信息
        User user = getById(userId);
        user.setUserPassword(newPassword);

        updateById(user);
        return ResponseResult.okResult();
    }

    //用户信息展示
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

            //通过用户id，从user_role表中找到roleId
            LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userRoleLambdaQueryWrapper.eq(UserRole::getUserId,userVo.getUserId());
            Integer roleId = userRoleMapper.selectOne(userRoleLambdaQueryWrapper).getRoleId();

            //通过roleId,得到roleName
            userVo.setRoleName(roleMapper.selectById(roleId).getRoleName());

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
    @Transactional
    public ResponseResult addUser(UserAddDto userAddDto) {
//        //密码加密处理
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //根据部门名称查询部门Id,并将其封装到userAddDto中
        LambdaQueryWrapper<Department> departmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        departmentLambdaQueryWrapper.eq(Department::getDepartmentName,userAddDto.getDepartmentName());
        Department department = departmentMapper.selectOne(departmentLambdaQueryWrapper);
        userAddDto.setDepartmentId(department.getDepartmentId());

        //保存到user表中
        User user = BeanCopyUtils.copyBean(userAddDto, User.class);
        save(user);

        //根据user的邮箱，查询其Id
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserEmail,user.getUserEmail());
        Integer userId = getOne(userLambdaQueryWrapper).getUserId();

        //根据userAddDto中的角色名称，得到角色Id
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName,userAddDto.getRoleName());
        Integer roleId = roleMapper.selectOne(roleLambdaQueryWrapper).getRoleId();

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);

//        if(user.getRoleIds()!=null&&user.getRoleIds().length>0){
//            insertUserRole(user);
//        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserInfoAdmin(Integer userId) {


        User user = getById(userId);

        UserInfoAdminVo userInfoAdminVo = BeanCopyUtils.copyBean(user, UserInfoAdminVo.class);

        //通过用户id，从user_role表中找到roleId
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getUserId,userId);
        Integer roleId = userRoleMapper.selectOne(userRoleLambdaQueryWrapper).getRoleId();

        //通过roleId,得到roleName
        userInfoAdminVo.setRoleName(roleMapper.selectById(roleId).getRoleName());

        //通过部门Id,得到用户部门名称,并封装到vo对象中
        userInfoAdminVo.setDepartmentName(departmentMapper.selectById(user.getDepartmentId()).getDepartmentName());

        return ResponseResult.okResult(userInfoAdminVo);
    }

    @Override
    @Transactional
    public void updateUser(UserChangeDto userChangeDto) {

        User user = BeanCopyUtils.copyBean(userChangeDto, User.class);


        //根据前端传来的部门名称，得到部门ID
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getDepartmentName,userChangeDto.getDepartmentName());
        Department department = departmentMapper.selectOne(queryWrapper);

        user.setDepartmentId(department.getDepartmentId());

        updateById(user);

        //根据userAddDto中的角色名称，得到角色Id
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName,userChangeDto.getRoleName());
        Integer roleId = roleMapper.selectOne(roleLambdaQueryWrapper).getRoleId();

        LambdaUpdateWrapper<UserRole> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        lambdaUpdateWrapper.eq(UserRole::getUserId,userChangeDto.getUserId());

        lambdaUpdateWrapper.set(UserRole::getRoleId,roleId);

        userRoleMapper.update(null,lambdaUpdateWrapper);

    }
}

