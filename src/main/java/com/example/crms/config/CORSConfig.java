package com.example.crms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  //允许跨域访问的路径
                .allowedOriginPatterns("*") //允许跨域使用的源
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")  //允许请求的方法
                .maxAge(16800)  //预检间隔时间
                .allowedHeaders("*")    //允许头部设置
                .allowCredentials(true);    //是否发送cookie
    }
}
