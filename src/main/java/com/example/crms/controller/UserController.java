package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.User;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.exception.SystemException;
import com.example.crms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //当前用户信息
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){

        return userService.userInfo();
    }

    //用户信息修改
    @PutMapping("/userInfo")
    public ResponseResult updateUserInfo(@RequestBody UserDto userDto){

        return userService.updateUserInfo(userDto);
    }

    //用户修改密码
    @PutMapping("/changePassword")
    public ResponseResult changePassword(String newPassword){

        return userService.changePassword(newPassword);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public ResponseResult list(UserDto userDto, Integer pageNum, Integer pageSize) {
        return userService.selectUserPage(userDto,pageNum,pageSize);
    }

    /**
     * 获取用户列表
     */

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody UserAddDto userAddDto)

    //确保邮箱唯一
    {
        if (!userService.checkEmailUnique(userAddDto)){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.addUser(userAddDto);
    }



}
