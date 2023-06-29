package com.example.crms.domain.vo;

import com.example.crms.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusVo {

    private String DepartmentName;
    private List<UserReducibleVo> userReducibleVos;
}
