package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * (UserRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-13 16:54:16
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}

