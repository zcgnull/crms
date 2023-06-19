package com.example.crms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserChangeDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.Schedule;
import com.example.crms.domain.entity.User;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-06-12 15:00:19
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    User selectOneByEmail(String email);

    boolean registerUser(User newUser);

    boolean changePassword(User user);

    ResponseResult updateUserInfo(UserDto userDto);

    ResponseResult changePassword(String newPassword);

    ResponseResult selectUserPage(UserDto userDto, Integer pageNum, Integer pageSize);

    boolean checkEmailUnique(UserAddDto userAddDto);

    ResponseResult addUser(UserAddDto userAddDto);

    ResponseResult getUserInfoAdmin(Integer userId);

    void updateUser(UserChangeDto userChangeDto);

    ResponseResult addStatus(Schedule schedule);

    ResponseResult getStatus(Integer pageNum, Integer pageSize);

    ResponseResult statusEdit(Schedule schedule);

    ResponseResult statusUser(Schedule schedule);
}

