package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (Role)表服务接口
 *
 * @author makejava
 * @since 2023-06-13 16:46:08
 */
public interface RoleService extends IService<Role> {

    List<String> getAllRoleNames();
}

