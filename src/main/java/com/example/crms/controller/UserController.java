package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.UserAddDto;
import com.example.crms.domain.dto.UserChangeDto;
import com.example.crms.domain.dto.UserDto;
import com.example.crms.domain.dto.*;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.enums.AppHttpCodeEnum;
import com.example.crms.exception.SystemException;
import com.example.crms.mapper.RoleMapper;
import com.example.crms.mapper.UserRoleMapper;
import com.example.crms.service.UserRoleService;
import com.example.crms.service.MailService;
import com.example.crms.service.UserService;
import com.example.crms.utils.SecurityUtils;
import io.swagger.models.auth.In;
import com.example.crms.utils.JWTUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.example.crms.utils.EmailUtils.generateEmailCaptcha;
import static com.example.crms.utils.EmailUtils.isValidEmail;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @PostMapping("/login")
    @ApiOperation("登录")
    public ResponseResult login(@RequestBody LoginUserDto userDto){
        //根据邮箱查找用户
        if (userDto.getUserEmail() == null){
            return ResponseResult.errorResult(401, "邮箱不能为空");
        }
        if (!isValidEmail(userDto.getUserEmail())){
            return ResponseResult.errorResult(402, "邮箱格式不正确");
        }
        User user = userService.selectOneByEmail(userDto.getUserEmail());
        if (user != null){
            if (user.getUserPassword().equals(userDto.getUserPassword())){
                String token = JWTUtils.generateToken(String.valueOf(user.getUserId()));
                return ResponseResult.okResult(200, "登录成功").ok(token);
            }
        }
        return ResponseResult.errorResult(400, "登录失败,账户或密码不正确");
    }


    @PostMapping("/register")
    @ApiOperation("注册")
    public ResponseResult register(@RequestBody RegisterUserDto registerUserDto, HttpSession session){
        User user = userService.selectOneByEmail(registerUserDto.getUserEmail());
        if (user == null){
            String inputCode = registerUserDto.getCode();
            String code = (String) session.getAttribute("code");
            if (code == null){
                return ResponseResult.errorResult(400, "注册失败,请重新获取验证码");
            }
            if (inputCode == null){
                session.setAttribute("code", null);
                return ResponseResult.errorResult(400, "注册失败,验证码不能为空");
            }
            if (inputCode.equals(code)) {
                if(registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())){
                    // 验证通过
                    //通过事务改各个表来完成注册事件
                    User newUser = new User();
                    newUser.setUserName(registerUserDto.getUserName());
                    newUser.setUserEmail(registerUserDto.getUserEmail());
                    newUser.setUserPassword(registerUserDto.getPassword());
                    newUser.setDepartmentId(registerUserDto.getDepartmentId());
                    boolean result = userService.registerUser(newUser);
                    if (result){
                        return ResponseResult.okResult(200, "注册成功");
                    } else {
                        return ResponseResult.errorResult(400, "注册失败,请重试");
                    }
                } else {
                    return ResponseResult.errorResult(400, "注册失败,两次密码不一致");
                }
            } else {
                // 验证失败
                return ResponseResult.errorResult(400, "注册失败,验证码错误");
            }
        } else {
            return ResponseResult.errorResult(400, "注册失败,邮箱已被使用");
        }
    }

    @GetMapping("/verify")
    @ApiOperation("发送邮箱验证码")
    public ResponseResult mailVerify(String email, HttpSession session){
        if (email == null){
            return ResponseResult.errorResult(401, "邮箱不能为空");
        }
        if (!isValidEmail(email)){
            return ResponseResult.errorResult(402, "邮箱格式不正确");
        }
        // 生成随机验证码
        String code = generateEmailCaptcha(5);
        try {
            // 发送验证码到用户邮箱
            mailService.sendMail(email, "邮箱验证", "您的验证码为：" + code);
            // 将验证码存入 session 中
            session.setAttribute("code", code);
            return ResponseResult.okResult(200, "邮件发送成功").ok(code);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseResult.errorResult(400, "邮件发送失败，请重试");
        }
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
