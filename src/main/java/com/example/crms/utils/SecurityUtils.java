package com.example.crms.utils;

import com.example.crms.domain.entity.LoginUser;
import com.example.crms.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 获取用户
     **/

    public static LoginUser getLoginUser()
    {

        return (LoginUser) getAuthentication().getPrincipal();
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

//    public static Boolean isAdmin(){
//        Integer id = getLoginUser().getUser().getUserId();
//        return id != null && id.equals(1L);
//    }

    public static Integer getUserId() {

        return getLoginUser().getUser().getUserId();
    }
    public static User getUser() {

        return getLoginUser().getUser();
    }
}