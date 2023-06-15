package com.example.crms.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 如果想指定外部的文件目录也很简单，直接通过addResourceLocations方法指定即可：
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/roomPic/**")
                .addResourceLocations("file:"+System.getProperty("user.dir")+"/roomPic/");

    }
}
