package com.example.crms.config;

import com.example.crms.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启权限认证
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    //创建BCryptPasswordEncoder注入容器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login", "/user/register", "/user/forget", "/user/verify","/role/allRoleNames", "/department/allDepartmentNames").anonymous()
//                .antMatchers("/testCors").hasAuthority("system:dept:list222")
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //添加jwt过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //配置异常处理器
        http.exceptionHandling()
                //配置认证失败处理器
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        //允许跨域
        http.cors();
    }

    // 放行静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/swagger-ui.html/**","/css/**","/images/**","/plugins/**","/webjars/**",
                "/swagger-resources/**","/v2/**","/roomPic/**"
        );
    }
//
//    // 放行系统资源
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                //登录请求必须放行
//                .antMatchers("/user/login","/user/logout","/user/verify","/user/register","/user/forget","/room/**").permitAll()
//                //为了测试，使得查询登录用户信息无需认证
//                .antMatchers("/user/userInfo").anonymous()
//                //修改密码，仅供测试
//                .antMatchers("/user/changePassword").anonymous()
//                //查询用户信息、新增用户、删除用户、查询所有角色、部门名称，仅供测试
//                .antMatchers("/user/list","/user/add","/role/allRoleNames","/user/userIds","/department/allDepartmentNames","/user/getUserInfoAdmin","/user/edit").anonymous()
//                //除了以上资源，剩下的http资源都必须登录后才能访问
//                .anyRequest().authenticated();
//
//        //关闭ccrf过滤器
//        http.csrf().disable();
//
////        //解决跨域
////        http.cors();
////
////        http.addFilter(new MyBasicAuthenticationFilter(authenticationManager()));
//    }

}
