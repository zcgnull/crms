package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.mapper.UserRoleMapper;
import com.example.crms.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * (UserRole)表服务实现类
 *
 * @author makejava
 * @since 2023-06-13 16:54:23
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public void removeUserRole(List<Integer> userIds) {
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.in(UserRole::getUserId,userIds);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleLambdaQueryWrapper);

        List<Integer> ids = new ArrayList<>();

        for (UserRole userRole : userRoles
             ) {
            ids.add(userRole.getId());
        }

        removeByIds(ids);
    }
}

