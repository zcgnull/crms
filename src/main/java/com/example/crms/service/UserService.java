package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.User;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-06-12 15:00:19
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();
}

