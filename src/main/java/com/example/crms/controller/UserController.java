package com.example.crms.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserChangeDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.exception.SystemException;
import com.example.crms.mapper.RoleMapper;
import com.example.crms.mapper.UserRoleMapper;
import com.example.crms.service.UserRoleService;
import com.example.crms.service.UserService;
import com.example.crms.utils.SecurityUtils;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

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


    /**
     * 删除用户
     */
    @DeleteMapping("/userIds")
    public ResponseResult remove(@RequestParam List<Integer> userIds) {
//        if(userIds.contains(SecurityUtils.getUserId())){
//            return ResponseResult.errorResult(500,"不能删除当前你正在使用的用户");
//        }
        System.out.println(userIds.toString());
        userService.removeByIds(userIds);
        userRoleService.removeUserRole(userIds);
        return ResponseResult.okResult();
    }

    /**
     * 修改用户：根据用户编号获取详细信息
     */
    @GetMapping("/getUserInfoAdmin")
    public ResponseResult getUserInfoAdmin(Integer userId)
    {
        return userService.getUserInfoAdmin(userId);

    }

    /**
     * 修改用户
     */
    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody UserChangeDto userChangeDto) {
        userService.updateUser(userChangeDto);
        return ResponseResult.okResult();
    }


}
