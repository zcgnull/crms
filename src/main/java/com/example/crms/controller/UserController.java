package com.example.crms.controller;

import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.dto.ForgetUserDto;
import com.example.crms.domain.dto.RegisterUserDto;
import com.example.crms.domain.dto.LoginUserDto;
import com.example.crms.domain.entity.User;
import com.example.crms.service.MailService;
import com.example.crms.service.UserService;

import com.example.crms.utils.JWTUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.example.crms.utils.EmailUtils.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

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

    @PostMapping("/forget")
    @ApiOperation("忘记密码")
    public ResponseResult forgetPassword(@RequestBody ForgetUserDto forgetUserDto, HttpSession session){
        User user = userService.selectOneByEmail(forgetUserDto.getUserEmail());
        if (user != null){
            String inputCode = forgetUserDto.getCode();
            String code = (String) session.getAttribute("code");
            if (code == null){
                return ResponseResult.errorResult(400, "更改失败,请重新获取验证码");
            }
            if (inputCode == null){
                session.setAttribute("code", null);
                return ResponseResult.errorResult(400, "更改失败,验证码不能为空");
            }
            if (inputCode.equals(code)) {
                if(forgetUserDto.getPassword().equals(forgetUserDto.getConfirmPassword())){
                    // 验证通过，更改密码
                    user.setUserPassword(forgetUserDto.getPassword());
                    boolean result = userService.changePassword(user);
                    if (result){
                        return ResponseResult.okResult(200, "修改成功");
                    } else {
                        return ResponseResult.errorResult(400, "修改失败,请重试");
                    }
                } else {
                    return ResponseResult.errorResult(400, "修改失败,两次密码不一致");
                }
            } else {
                // 验证失败
                return ResponseResult.errorResult(400, "修改失败,验证码错误");
            }
        } else {
            return ResponseResult.errorResult(400, "不存在此用户");
        }
    }

    @GetMapping("/logout")
    @ApiOperation("退出登录")
    public ResponseResult logout(HttpServletRequest request){
        String token = request.getHeader("token");
        System.out.println(token);
        return ResponseResult.okResult(200, "退出登录");
    }
}
