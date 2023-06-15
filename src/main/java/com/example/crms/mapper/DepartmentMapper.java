package com.example.crms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crms.domain.entity.Department;
import org.apache.ibatis.annotations.Mapper;

/**
 * (Department)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-12 14:37:20
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

}

