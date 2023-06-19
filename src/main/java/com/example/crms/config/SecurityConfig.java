package com.example.crms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 放行静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/swagger-ui.html/**","/css/**","/images/**","/plugins/**","/webjars/**",
                "/swagger-resources/**","/v2/**","/roomPic/**"
        );
    }

    // 放行系统资源
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //登录请求必须放行
                .antMatchers("/user/login","/user/logout","/user/verify","/user/register","/user/forget","/room/**").permitAll()
                //为了测试，使得查询登录用户信息无需认证
                .antMatchers("/user/userInfo").anonymous()
                //修改密码，仅供测试
                .antMatchers("/user/changePassword").anonymous()
                //查询用户信息、新增用户、删除用户、查询所有角色、部门名称，仅供测试
                .antMatchers("/user/list","/user/add","/role/allRoleNames","/user/userIds","/department/allDepartmentNames","/user/getUserInfoAdmin","/user/edit","/user/getStatus","/user/addStatus"
                        ,"/user/deleteStatus","/user/statusEdit","/user/statusUser").anonymous()
                .antMatchers("/role/**").anonymous()
                //除了以上资源，剩下的http资源都必须登录后才能访问
                .anyRequest().authenticated();

        //关闭ccrf过滤器
        http.csrf().disable();

//        //解决跨域
//        http.cors();
//
//        http.addFilter(new MyBasicAuthenticationFilter(authenticationManager()));
    }

}
