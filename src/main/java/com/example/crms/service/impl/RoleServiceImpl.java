package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Role;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.domain.vo.PageVo;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.mapper.RoleMapper;
import com.example.crms.mapper.UserRoleMapper;
import com.example.crms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * (Role)表服务实现类
 *
 * @author makejava
 * @since 2023-06-13 16:46:08
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;



    @Override
    public List<String> getAllRoleNames() {

        //查询所有角色信息
        List<Role> roles = roleMapper.selectList(null);

        //将所有角色信息中的角色名称封装到AllRoleNames集合中
        List<String> AllRoleNames = new ArrayList<>();
        for (Role role: roles
        ) {
            AllRoleNames.add(role.getRoleName());
        }

        return AllRoleNames;
    }

    @Override
    public ResponseResult pageRoleList(Integer pageNum, Integer pageSize, String roleName) {


        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(roleName), Role::getRoleName, roleName);

        Page page = new Page(pageNum, pageSize);
        Page page1 = page(page, queryWrapper);


        PageVo pageVo = new PageVo();
        pageVo.setTotal(page1.getTotal());
        pageVo.setRows(page1.getRecords());

        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addRole(Role role) {


        String roleName = role.getRoleName();

        //判断该角色名称是否已经存在
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName, roleName);

        if (roleMapper.selectOne(roleLambdaQueryWrapper) != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.RoleName_EXIST);
        }

        save(role);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateRole(Role role) {

        String roleName = role.getRoleName();

        //没有修改角色名，只修改了其它信息
        Role role1 = roleMapper.selectById(role.getRoleId());
        if (role1.getRoleName().equals(roleName)) {
            save(role);
            return ResponseResult.okResult();
        }

        //判断该角色名称是否已经存在
        LambdaQueryWrapper<Role> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(Role::getRoleName, roleName);

        if (roleMapper.selectOne(roleLambdaQueryWrapper) != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.RoleName_EXIST);
        }
        updateById(role);
        return ResponseResult.okResult();
    }

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public ResponseResult deleteRoleById(Integer roleId) {


        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getRoleId, roleId);

        List<UserRole> userRoles = userRoleMapper.selectList(userRoleLambdaQueryWrapper);
        if (userRoles.size() != 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.UserRole_EXIST);
        }
        removeById(roleId);
        return ResponseResult.okResult();
    }
}

