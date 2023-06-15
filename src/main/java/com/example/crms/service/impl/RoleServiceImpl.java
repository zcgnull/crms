package com.example.crms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.entity.Department;
import com.example.crms.domain.entity.Role;
import com.example.crms.mapper.RoleMapper;
import com.example.crms.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}

