package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.example.crms.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
