package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Role)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-13 16:45:10
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}

