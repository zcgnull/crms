package com.example.crms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crms.domain.ResponseResult;
import com.example.crms.domain.entity.User;
import com.example.crms.domain.entity.UserRole;
import com.example.crms.mapper.UserMapper;
import com.example.crms.mapper.UserRoleMapper;
import com.example.crms.service.UserService;
import com.example.crms.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-06-12 15:01:42
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;


    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Integer userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        //UserInfoVo vo = BeanCopyUtils.copyBean(user,UserInfoVo.class);
        return ResponseResult.okResult(new User());
    }

    @Override
    public User selectOneByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_email", email);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 注册新用户事务
     * @param newUser
     * @return
     */
    @Override
    public synchronized boolean registerUser(User newUser) {
        transactionTemplate.setTimeout(4000);
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus transactionStatus) {
                try {
                    userMapper.insert(newUser);
                    //注册时，默认角色为普通员工
                    UserRole userRole = new UserRole();
                    userRole.setUserId(newUser.getUserId());
                    userRole.setRoleId(3);
                    userRoleMapper.insert(userRole);
                    //.......   业务代码
                    return true;
                } catch (Exception e) {
                    System.out.println("错误为：  " + e);
                    //回滚
                    transactionStatus.setRollbackOnly();
                    return false;
                }
            }
        });
    }

    class MyException extends Exception {
        public MyException(String message) {
            super(message);
        }
    }


}







