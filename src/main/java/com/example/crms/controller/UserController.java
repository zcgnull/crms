package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.User;
import com.example.crms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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




}
