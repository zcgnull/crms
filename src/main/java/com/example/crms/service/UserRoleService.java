package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.entity.UserRole;

import java.util.List;

/**
 * (UserRole)表服务接口
 *
 * @author makejava
 * @since 2023-06-13 16:54:23
 */
public interface UserRoleService extends IService<UserRole> {

    void removeUserRole(List<Integer> userIds);
}

